package jsettlers.main.android.resources;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;

import jsettlers.main.android.R;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;

public class ResourceUpdater implements Runnable {

	private static class ServerData {
		private final String revision;
		private final long size;

		public ServerData(String readData) throws IOException {
			String[] entries = readData.split("\n");
			if (entries.length < 2) {
				throw new IOException("Server has not sent enogh data.");
			}
			if (!entries[1].matches("\\d+")) {
				throw new IOException("Size is not a number.");
			}
			revision = entries[0];
			size = Long.parseLong(entries[1]);
		}
	}

	private static final int REVISION = 1; // FIXME set a correct revision / build number
	private static final String RESOURCE_PREFIX = "";
	private static final String SERVER_ROOT = "https://michael2402.homeip.net/jsettlers/";
	/**
	 * The current program revision to force an update on program update.
	 */
	private static final String PREF_REVISION = "rev";
	/**
	 * If an update needs to be forced on next start.
	 */
	private static final String PREF_OUTDATED = "force";
	/**
	 * The revision of the resources we got.
	 */
	private static final String PREF_RESOURCEVERSION = "resources";
	private final Resources resources;
	private final File destdir;

	private boolean isUpdating;
	private ServerData serverData = null;
	private final SharedPreferences prefs;
	private final Object updateMutex = new Object();

	public ResourceUpdater(Context context, File destdir) {
		this.resources = context.getResources();
		this.prefs = context.getSharedPreferences("resupdate", 0);
		this.destdir = destdir;

		int revHash = REVISION;
		if (prefs.getInt(PREF_REVISION, -1) != revHash) {
			requireUpdate();
		}
	}

	@Override
	public void run() {
		try {
			synchronized (updateMutex) {
				DefaultHttpClient httpClient = createClient();

				serverData = loadRevision(httpClient);

				String myversion = prefs.getString(PREF_RESOURCEVERSION, "");
				boolean serverrevIsNewer = serverData != null
						&& !serverData.revision.equals(myversion);
				if (serverrevIsNewer) {
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	private void requireUpdate() {
		prefs.edit().putBoolean(PREF_OUTDATED, true).commit();
	}

	public void startUpdate(final UpdateListener listener) {
		if (isUpdating()) {
			// bad. really bad.
			return;
		}

		setUpdating(true);
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					synchronized (updateMutex) {
						updateFiles(createClient(), listener);
					}
				} catch (Throwable t) {
					t.printStackTrace();
				}
				setUpdating(false);
				// TODO: i18n
				listener.setProgressState("Updating", 1);
				if (listener != null) {
					listener.resourceUpdateFinished();
				}

			}
		}, "resource-update").start();
	}

	private void updateFiles(DefaultHttpClient httpClient, UpdateListener c)
			throws IOException, ClientProtocolException {
		// TODO: i18n
		c.setProgressState("Updating", -1);

		if (serverData == null) {
			serverData = loadRevision(httpClient);
		}

		final String url = SERVER_ROOT + "resources.zip";
		HttpGet httpRequest = new HttpGet(url);
		HttpResponse response = httpClient.execute(httpRequest);
		InputStream compressed = response.getEntity()
				.getContent();
		ZipInputStream inputStream = new ZipInputStream(compressed);

		try {

			int files = 0;

			byte[] buffer = new byte[1024];

			long read = 0;

			ZipEntry entry;
			while ((entry = inputStream.getNextEntry()) != null) {
				String name = entry.getName();
				// TODO: i18n
				c.setProgressState("Updating",
						(float) read / serverData.size);
				System.out.println("Size: " + read + " of " + serverData.size);

				if (name.startsWith(RESOURCE_PREFIX)) {
					String outfilename = destdir.getAbsolutePath() + "/"
							+ name.substring(RESOURCE_PREFIX.length());
					File outfile = new File(outfilename);
					if (entry.isDirectory()) {
						if (outfile.exists() && !outfile.isDirectory()) {
							outfile.delete();
						}
						if (!outfile.isDirectory()) {
							outfile.mkdirs();
						}
					} else {
						File tmpFile = new File(outfilename + ".tmp");
						tmpFile.getParentFile().mkdirs();
						tmpFile.deleteOnExit(); // <- if something fails
						FileOutputStream out = new FileOutputStream(tmpFile);

						while (true) {
							int len = inputStream.read(buffer);
							if (len <= 0) {
								break;
							}
							read += len;
							out.write(buffer, 0, len);
						}
						out.close();
						tmpFile.renameTo(outfile);
						files++;
					}
				}
			}
			System.out.println("Updated " + files + " files");

			prefs.edit().putInt(PREF_REVISION, REVISION)
					.putBoolean(PREF_OUTDATED, false)
					.putString(PREF_RESOURCEVERSION, serverData.revision).commit();
		} catch (Throwable t) {
			t.printStackTrace();
		}
		setUpdating(false);
	}

	private static ServerData loadRevision(DefaultHttpClient httpClient)
			throws IOException, ClientProtocolException {
		final String url = SERVER_ROOT + "revision.txt";
		HttpGet httpRequest = new HttpGet(url);
		HttpResponse response = httpClient.execute(httpRequest);
		InputStream inputStream = response.getEntity().getContent();
		return new ServerData(getString(inputStream));
	}

	private static String getString(InputStream inputStream) {
		return new Scanner(inputStream).useDelimiter("\\A").next();
	}

	private DefaultHttpClient createClient() throws KeyStoreException,
			NoSuchAlgorithmException, CertificateException, IOException,
			KeyManagementException, UnrecoverableKeyException {
		HostnameVerifier hostnameVerifier = SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;

		DefaultHttpClient client = new DefaultHttpClient();

		KeyStore truststore = KeyStore.getInstance("BKS");
		InputStream in = resources.openRawResource(R.raw.certs);

		truststore.load(in, "F2rORYtG".toCharArray());

		SSLSocketFactory socketFactory = new SSLSocketFactory(truststore);

		SchemeRegistry registry = new SchemeRegistry();
		socketFactory
				.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);
		registry.register(new Scheme("https", socketFactory, 443));
		SingleClientConnManager mgr = new SingleClientConnManager(
				client.getParams(), registry);
		DefaultHttpClient httpClient = new DefaultHttpClient(mgr,
				client.getParams());

		// Set verifier
		HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
		return httpClient;
	}

	public synchronized boolean isUpdating() {
		return isUpdating;
	}

	public synchronized void waitUntilUpdateFinished()
			throws InterruptedException {
		while (isUpdating) {
			this.wait();
		}
	}

	private synchronized void setUpdating(boolean isUpdating) {
		this.isUpdating = isUpdating;
		this.notifyAll();
	}

	public boolean needsUpdate() {
		return prefs.getBoolean(PREF_OUTDATED, true);
	}
}

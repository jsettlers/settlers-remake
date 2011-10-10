package jsettlers.common.buildings.loader;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import jsettlers.common.buildings.RelativeBricklayer;
import jsettlers.common.buildings.RelativeStack;
import jsettlers.common.buildings.jobs.IBuildingJob;
import jsettlers.common.images.EImageLinkType;
import jsettlers.common.images.ImageLink;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.RelativePoint;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * This class represents a building's xml file.
 * 
 * @author michael
 */
public class BuildingFile implements BuildingJobDataProvider {

	private static final String BUILDING_DTD = "building.dtd";
	private static final String DATA_DIR = "../data/";
	private static final String TAG_JOB = "job";
	private static final String TAG_STARTJOB = "startjob";
	private static final String TAG_DOOR = "door";
	private static final String TAG_BLOCKED = "blocked";
	private static final String TAG_STACK = "stack";
	private static final String ATTR_JOBNAME = "name";
	private static final String ATTR_DX = "dx";
	private static final String ATTR_DY = "dy";
	private static final String ATTR_MATERIAl = "material";
	private static final String ATTR_BUILDREQUIRED = "buildrequired";
	private static final String TAG_WORKCENTER = "workcenter";
	private static final String TAG_FLAG = "flag";
	private static final String TAG_BRICKLAYER = "bricklayer";
	private static final String ATTR_DIRECTION = "direction";
	private static final Object TAG_BUILDMARK = "buildmark";
	private static final Object TAG_IMAGE = "image";
	private final Document document;

	private final ArrayList<RelativePoint> blocked =
	        new ArrayList<RelativePoint>();

	private final ArrayList<RelativePoint> protectedTiles =
	        new ArrayList<RelativePoint>();

	private final Hashtable<String, JobElementWrapper> jobElements =
	        new Hashtable<String, JobElementWrapper>();

	private String startJobName = "";
	private RelativePoint door = new RelativePoint(0, 0);
	private IBuildingJob startJob = null;

	private EMovableType workerType;
	private ArrayList<RelativeStack> stacks = new ArrayList<RelativeStack>();
	private ArrayList<RelativeBricklayer> bricklayers =
	        new ArrayList<RelativeBricklayer>();

	private int workradius;
	private RelativePoint workCenter = new RelativePoint(0, 0);
	private RelativePoint flag = new RelativePoint(0, 0);
	private ArrayList<RelativePoint> buildmarks =
	        new ArrayList<RelativePoint>();
	private ImageLink guiimage = new ImageLink(EImageLinkType.GUI, 1, 0, 0);
	private ArrayList<ImageLink> images = new ArrayList<ImageLink>();
	private ArrayList<ImageLink> buildImages = new ArrayList<ImageLink>();

	public BuildingFile(String buildingName) {
		document = createDocument(buildingName);

		readDocument(buildingName);
	}

	private void readDocument(String buildingName) {
		Element root = document.getDocumentElement();
		readAttributes(root);

		NodeList nodes = root.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) node;
				readElement(buildingName, element);
			}
		}
	}

	private void readElement(String buildingName, Element element) {
		String tagName = element.getTagName();
		if (TAG_JOB.equals(tagName)) {
			String name = element.getAttribute(ATTR_JOBNAME);
			jobElements.put(name, new JobElementWrapper(element));
		} else if (TAG_STARTJOB.equals(tagName)) {
			startJobName = element.getAttribute(ATTR_JOBNAME);
		} else if (TAG_DOOR.equals(tagName)) {
			door = readRelativeTile(buildingName, element);
		} else if (TAG_WORKCENTER.equals(tagName)) {
			workCenter = readRelativeTile(buildingName, element);
		} else if (TAG_FLAG.equals(tagName)) {
			flag = readRelativeTile(buildingName, element);
		} else if (TAG_BLOCKED.equals(tagName)) {
			RelativePoint point = readRelativeTile(buildingName, element);
			if ("true".equals(element.getAttribute("block"))) {
				blocked.add(point);
			}
			protectedTiles.add(point);
		} else if (TAG_STACK.equals(tagName)) {
			readRelativeStack(buildingName, element);
		} else if (TAG_BRICKLAYER.equals(tagName)) {
			readRelativeBricklayer(buildingName, element);
		} else if (TAG_IMAGE.equals(tagName)) {
			readImageLink(buildingName, element);
		} else if (TAG_BUILDMARK.equals(tagName)) {
			buildmarks.add(readRelativeTile(buildingName, element));
		}
	}

	private void readImageLink(String buildingName, Element element) {
		try {
			int file = Integer.parseInt(element.getAttribute("file"));
			int sequence = Integer.parseInt(element.getAttribute("sequence"));
			int image = Integer.parseInt(element.getAttribute("image"));
			EImageLinkType type =
			        EImageLinkType.valueOf(element.getAttribute("type"));
			ImageLink imageLink = new ImageLink(type, file, sequence, image);
			String forState = element.getAttribute("for");
			if ("GUI".equals(forState)) {
				guiimage = imageLink;
			} else if ("BUILD".equals(forState)) {
				buildImages.add(imageLink);
			}else {
				images.add(imageLink);
			}
		} catch (NumberFormatException e) {
			System.err
			        .println("Warning: illegal number for image link attribute, in definiton for "
			                + buildingName);
		} catch (IllegalArgumentException e) {
			System.err.println("Illegal image link name in " + buildingName);
		}
	}

	private void readRelativeBricklayer(String buildingName, Element element) {
		try {
			int dx = Integer.parseInt(element.getAttribute(ATTR_DX));
			int dy = Integer.parseInt(element.getAttribute(ATTR_DY));
			EDirection direction =
			        EDirection.valueOf(element.getAttribute(ATTR_DIRECTION));

			bricklayers.add(new RelativeBricklayer(dx, dy, direction));

		} catch (NumberFormatException e) {
			System.err
			        .println("Warning: illegal number for stack attribute, in definiton for "
			                + buildingName);
		} catch (IllegalArgumentException e) {
			System.err.println("Illegal material name in " + buildingName);
		}
	}

	private RelativePoint readRelativeTile(String buildingName, Element element) {
		try {
			int dx = Integer.parseInt(element.getAttribute(ATTR_DX));
			int dy = Integer.parseInt(element.getAttribute(ATTR_DY));

			return new RelativePoint(dx, dy);

		} catch (NumberFormatException e) {
			System.err
			        .println("Warning: illegal number for relative tile attribute, in definiton for "
			                + buildingName);
			return new RelativePoint(0, 0);
		}
	}

	private void readRelativeStack(String buildingName, Element element) {
		try {
			int dx = Integer.parseInt(element.getAttribute(ATTR_DX));
			int dy = Integer.parseInt(element.getAttribute(ATTR_DY));
			EMaterialType type =
			        EMaterialType.valueOf(element.getAttribute(ATTR_MATERIAl));
			short requiredForBuild =
			        Short.parseShort(element.getAttribute(ATTR_BUILDREQUIRED));

			stacks.add(new RelativeStack(dx, dy, type, requiredForBuild));

		} catch (NumberFormatException e) {
			System.err
			        .println("Warning: illegal number for stack attribute, in definiton for "
			                + buildingName);
		} catch (IllegalArgumentException e) {
			System.err.println("Illegal material name in " + buildingName);
		}
	}

	private void readAttributes(Element root) {
		String workerName = root.getAttribute("worker");
		if (workerName == "") {
			this.workerType = null;
		} else {
			try {
				this.workerType = EMovableType.valueOf(workerName);
			} catch (IllegalArgumentException e) {
				System.err.println("Illegal worker name: " + workerName);
				this.workerType = EMovableType.BEARER;
			}
		}
		if (root.getAttribute("workradius").matches("\\d+")) {
			this.workradius = Integer.parseInt(root.getAttribute("workradius"));
		}
	}

	private Document createDocument(String buildingName) {
		InputStream stream =
		        getClass().getResourceAsStream(
		                DATA_DIR + buildingName.toLowerCase() + ".xml");

		DocumentBuilder builder = getDocumentBuilder();

		Document document;
		try {
			document = builder.parse(stream);
			stream.close();
		} catch (SAXException e) {
			document = createEmptyDocument(builder);
		} catch (IllegalArgumentException e) {
			document = createEmptyDocument(builder);
		} catch (IOException e) {
			document = createEmptyDocument(builder);
		}
		return document;
	}

	private DocumentBuilder getDocumentBuilder() {
		final DocumentBuilderFactory factory =
		        DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			builder.setEntityResolver(new EntityResolver() {

				@Override
				public InputSource resolveEntity(String publicId,
				        String systemId) throws SAXException, IOException {
					if (systemId.contains(BUILDING_DTD)) {
						return new InputSource(this.getClass()
						        .getResourceAsStream(DATA_DIR + BUILDING_DTD));
					} else {
						return null;
					}
				}
			});
		} catch (final ParserConfigurationException e) {
			throw new RuntimeException("ParserConfigurationException: "
			        + e.getMessage());
		}
		return builder;
	}

	private Document createEmptyDocument(DocumentBuilder builder) {
		Document document;
		document = builder.newDocument();
		document.appendChild(document.createElement("building"));
		return document;
	}

	public IBuildingJob getStartJob() {
		if (startJob == null) {
			try {
				if (startJobName == "") {
					startJob = SimpleBuildingJob.createFallback();
				} else {
					startJob =
					        SimpleBuildingJob.createLinkedJobs(this,
					                startJobName);
				}
			} catch (Exception e) {
				System.err
				        .println("Error while creating job list, using fallback. Message: "
				                + e);
				startJob = SimpleBuildingJob.createFallback();
			}
		}
		return startJob;
	}

	public RelativePoint getDoor() {
		return door;
	}

	@Override
	public BuildingJobData getJobData(String name) {
		return jobElements.get(name);
	}

	public EMovableType getWorkerType() {
		return workerType;
	}

	public RelativePoint[] getProtectedTiles() {
		return protectedTiles.toArray(new RelativePoint[protectedTiles.size()]);
	}

	public RelativePoint[] getBlockedTiles() {
		return blocked.toArray(new RelativePoint[blocked.size()]);
	}

	public RelativeStack[] getStacks() {
		return stacks.toArray(new RelativeStack[stacks.size()]);
	}

	public RelativeBricklayer[] getBricklayers() {
		return bricklayers.toArray(new RelativeBricklayer[bricklayers.size()]);
	}

	public short getWorkradius() {
		return (short) workradius;
	}

	public RelativePoint getWorkcenter() {
		return workCenter;
	}

	public RelativePoint getFlag() {
		return flag;
	}

	public ImageLink[] getImages() {
		return images.toArray(new ImageLink[images.size()]);
	}

	public ImageLink[] getBuildImages() {
		return buildImages.toArray(new ImageLink[buildImages.size()]);
	}

	public ImageLink getGuiImage() {
		return guiimage;
	}

	public RelativePoint[] getBuildmarks() {
		return buildmarks.toArray(new RelativePoint[buildmarks.size()]);
	}
}

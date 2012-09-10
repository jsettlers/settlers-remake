package jsettlers.common.buildings.loader;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;

import jsettlers.common.buildings.OccupyerPlace;
import jsettlers.common.buildings.OccupyerPlace.ESoldierType;
import jsettlers.common.buildings.RelativeBricklayer;
import jsettlers.common.buildings.RelativeStack;
import jsettlers.common.buildings.jobs.IBuildingJob;
import jsettlers.common.images.EImageLinkType;
import jsettlers.common.images.ImageLink;
import jsettlers.common.images.OriginalImageLink;
import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.RelativePoint;
import jsettlers.common.resources.ResourceManager;

import org.xml.sax.Attributes;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * This class represents a building's xml file.
 * 
 * @author michael
 */
public class BuildingFile implements BuildingJobDataProvider {

	private static final String BUILDING_DTD = "building.dtd";

	private static final String DATA_DIR = "buildings/";
	private static final String TAG_BUILDING = "building";
	private static final String TAG_JOB = "job";
	private static final String TAG_STARTJOB = "startjob";
	private static final String TAG_DOOR = "door";
	private static final String TAG_BLOCKED = "blocked";
	private static final String TAG_STACK = "stack";
	private static final String TAG_OCCUPYER = "occupyer";
	private static final String ATTR_JOBNAME = "name";
	private static final String ATTR_DX = "dx";
	private static final String ATTR_DY = "dy";
	private static final String ATTR_MATERIAl = "material";
	private static final String ATTR_BUILDREQUIRED = "buildrequired";
	private static final String TAG_WORKCENTER = "workcenter";
	private static final String TAG_FLAG = "flag";
	private static final String TAG_BRICKLAYER = "bricklayer";
	private static final String ATTR_DIRECTION = "direction";
	private static final String TAG_BUILDMARK = "buildmark";
	private static final String TAG_ATTACKER = "attacker";
	private static final String TAG_IMAGE = "image";
	private static final String TAG_GROUNDTYE = "ground";

	private final ArrayList<RelativePoint> blocked = new ArrayList<RelativePoint>();

	private final ArrayList<RelativePoint> protectedTiles = new ArrayList<RelativePoint>();

	private final Hashtable<String, JobElementWrapper> jobElements = new Hashtable<String, JobElementWrapper>();

	private String startJobName = "";
	private RelativePoint door = new RelativePoint(0, 0);
	private IBuildingJob startJob = null;

	private EMovableType workerType;
	private ArrayList<RelativeStack> stacks = new ArrayList<RelativeStack>();
	private ArrayList<RelativeBricklayer> bricklayers = new ArrayList<RelativeBricklayer>();

	private int workradius;
	private RelativePoint workCenter = new RelativePoint(0, 0);
	private RelativePoint flag = new RelativePoint(0, 0);
	private ArrayList<RelativePoint> buildmarks = new ArrayList<RelativePoint>();
	private ArrayList<RelativePoint> attackers = new ArrayList<RelativePoint>();
	private ImageLink guiimage = new OriginalImageLink(EImageLinkType.GUI, 1, 0, 0);
	private ArrayList<ImageLink> images = new ArrayList<ImageLink>();
	private ArrayList<ImageLink> buildImages = new ArrayList<ImageLink>();
	private ArrayList<ELandscapeType> groundtypes = new ArrayList<ELandscapeType>();
	private ArrayList<OccupyerPlace> occupyerplaces = new ArrayList<OccupyerPlace>();
	private short viewdistance = 0;
	private final String buildingName;

	public BuildingFile(String buildingName) {
		this.buildingName = buildingName;
		try {
			XMLReader xr = XMLReaderFactory.createXMLReader();
			xr.setContentHandler(new SaxHandler());
			xr.setEntityResolver(new EntityResolver() {
				@Override
				public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
					if (systemId.contains(BUILDING_DTD)) {
						return new InputSource(ResourceManager.getFile(DATA_DIR + BUILDING_DTD));
					} else {
						return null;
					}
				}
			});

			InputStream stream = ResourceManager.getFile(DATA_DIR + buildingName.toLowerCase() + ".xml");
			xr.parse(new InputSource(stream));
		} catch (Exception e) {
			System.err.println("Error loading building file for " + buildingName + ":" + e.getMessage());
			loadDefault();
		}
	}

	private class SaxHandler extends DefaultHandler {

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			String tagName = qName;
			if (TAG_BUILDING.equals(tagName)) {
				readAttributes(attributes);
			} else if (TAG_JOB.equals(tagName)) {
				String name = attributes.getValue(ATTR_JOBNAME);
				jobElements.put(name, new JobElementWrapper(attributes));
			} else if (TAG_STARTJOB.equals(tagName)) {
				startJobName = attributes.getValue(ATTR_JOBNAME);
			} else if (TAG_DOOR.equals(tagName)) {
				door = readRelativeTile(attributes);
			} else if (TAG_WORKCENTER.equals(tagName)) {
				workCenter = readRelativeTile(attributes);
			} else if (TAG_FLAG.equals(tagName)) {
				flag = readRelativeTile(attributes);
			} else if (TAG_BLOCKED.equals(tagName)) {
				RelativePoint point = readRelativeTile(attributes);
				if ("true".equals(attributes.getValue("block"))) {
					blocked.add(point);
				}
				protectedTiles.add(point);
			} else if (TAG_STACK.equals(tagName)) {
				readRelativeStack(attributes);
			} else if (TAG_BRICKLAYER.equals(tagName)) {
				readRelativeBricklayer(attributes);
			} else if (TAG_IMAGE.equals(tagName)) {
				readImageLink(attributes);
			} else if (TAG_BUILDMARK.equals(tagName)) {
				buildmarks.add(readRelativeTile(attributes));
			} else if (TAG_ATTACKER.equals(tagName)) {
				attackers.add(readRelativeTile(attributes));
			} else if (TAG_GROUNDTYE.equals(tagName)) {
				groundtypes.add(ELandscapeType.valueOf(attributes.getValue("groundtype")));
			} else if (TAG_OCCUPYER.equals(tagName)) {
				addOccupyer(attributes);
			}
		}
	}

	private void loadDefault() {
		blocked.add(new RelativePoint(0, 0));
		protectedTiles.add(new RelativePoint(0, 0));
		System.err.println("Building file defect: " + buildingName);
	}

	private void addOccupyer(Attributes attributes) {
		try {
			int x = parseOptionalInteger(attributes.getValue("offsetX"));
			int y = parseOptionalInteger(attributes.getValue("offsetY"));
			ESoldierType type = OccupyerPlace.ESoldierType.valueOf(attributes.getValue("type"));
			RelativePoint position = new RelativePoint(Short.parseShort(attributes.getValue("soldierX")), Short.parseShort(attributes
					.getValue("soldierY")));
			OccupyerPlace place = new OccupyerPlace(x, y, type, position, "true".equals(attributes.getValue("looksRight")));
			occupyerplaces.add(place);
		} catch (NumberFormatException e) {
			System.err.println("Warning: illegal number " + "for occupyer x/y attribute, in definiton for " + buildingName);
		} catch (IllegalArgumentException e) {
			System.err.println("Illegal occupyer position name in " + buildingName);
		}
	}

	/**
	 * If value != null, the value is parsed. Otherwise, 0 is returned.
	 * 
	 * @param value
	 * @return
	 * @throws NumberFormatException
	 */
	private int parseOptionalInteger(String value) throws NumberFormatException {
		if (value != null) {
			return Integer.parseInt(value);
		} else {
			return 0;
		}
	}

	private void readImageLink(Attributes attributes) {
		try {
			ImageLink imageLink;
			if (attributes.getIndex("name") < 0) {
				imageLink = getOriginalImageLink(attributes);
			} else {
				String name = attributes.getValue("name");
				int image = Integer.parseInt(attributes.getValue("image"));
				imageLink = ImageLink.fromName(name, image);
			}
			String forState = attributes.getValue("for");
			if ("GUI".equals(forState)) {
				guiimage = imageLink;
			} else if ("BUILD".equals(forState)) {
				buildImages.add(imageLink);
			} else {
				images.add(imageLink);
			}
		} catch (NumberFormatException e) {
			System.err.println("Warning: illegal number " + "for image link attribute, in definiton for " + buildingName);
		} catch (IllegalArgumentException e) {
			System.err.println("Illegal image link name in " + buildingName);
		}
	}

	private static OriginalImageLink getOriginalImageLink(Attributes attributes) {
		int file = Integer.parseInt(attributes.getValue("file"));
		int sequence = Integer.parseInt(attributes.getValue("sequence"));
		int image = Integer.parseInt(attributes.getValue("image"));
		EImageLinkType type = EImageLinkType.valueOf(attributes.getValue("type"));
		OriginalImageLink imageLink = new OriginalImageLink(type, file, sequence, image);
		return imageLink;
	}

	private void readRelativeBricklayer(Attributes attributes) {
		try {
			int dx = Integer.parseInt(attributes.getValue(ATTR_DX));
			int dy = Integer.parseInt(attributes.getValue(ATTR_DY));
			EDirection direction = EDirection.valueOf(attributes.getValue(ATTR_DIRECTION));

			bricklayers.add(new RelativeBricklayer(dx, dy, direction));

		} catch (NumberFormatException e) {
			System.err.println("Warning: illegal number for stack attribute, in definiton for " + buildingName);
		} catch (IllegalArgumentException e) {
			System.err.println("Illegal material name in " + buildingName);
		}
	}

	private RelativePoint readRelativeTile(Attributes attributes) {
		try {
			int dx = Integer.parseInt(attributes.getValue(ATTR_DX));
			int dy = Integer.parseInt(attributes.getValue(ATTR_DY));

			return new RelativePoint(dx, dy);

		} catch (NumberFormatException e) {
			System.err.println("Warning: illegal number " + "for relative tile attribute, in definiton for " + buildingName);
			return new RelativePoint(0, 0);
		}
	}

	private void readRelativeStack(Attributes attributes) {
		try {
			int dx = Integer.parseInt(attributes.getValue(ATTR_DX));
			int dy = Integer.parseInt(attributes.getValue(ATTR_DY));
			EMaterialType type = EMaterialType.valueOf(attributes.getValue(ATTR_MATERIAl));
			short requiredForBuild = Short.parseShort(attributes.getValue(ATTR_BUILDREQUIRED));

			stacks.add(new RelativeStack(dx, dy, type, requiredForBuild));

		} catch (NumberFormatException e) {
			System.err.println("Warning: illegal number " + "for stack attribute, in definiton for " + buildingName);
		} catch (IllegalArgumentException e) {
			System.err.println("Illegal material name in " + buildingName);
		}
	}

	/**
	 * Read from a building tag
	 * 
	 * @param attributes
	 */
	private void readAttributes(Attributes attributes) {
		String workerName = attributes.getValue("worker");
		if (workerName == null || workerName.isEmpty()) {
			this.workerType = null;
		} else {
			try {
				this.workerType = EMovableType.valueOf(workerName);
			} catch (IllegalArgumentException e) {
				System.err.println("Illegal worker name: " + workerName);
				this.workerType = EMovableType.BEARER;
			}
		}
		String workradius = attributes.getValue("workradius");
		if (workradius != null && workradius.matches("\\d+")) {
			this.workradius = Integer.parseInt(workradius);
		}
		String viewdistance = attributes.getValue("viewdistance");
		if (viewdistance != null && viewdistance.matches("\\d+")) {
			this.viewdistance = Short.parseShort(viewdistance);
		}
	}

	public IBuildingJob getStartJob() {
		if (startJob == null) {
			try {
				if (startJobName == null || startJobName.isEmpty()) {
					startJob = SimpleBuildingJob.createFallback();
				} else {
					startJob = SimpleBuildingJob.createLinkedJobs(this, startJobName);
				}
			} catch (Exception e) {
				System.err.println("Error while creating job list for " + buildingName + ", using fallback. Message: " + e);
				e.printStackTrace();
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

	public ELandscapeType[] getGroundtypes() {
		return groundtypes.toArray(new ELandscapeType[groundtypes.size()]);
	}

	public short getViewdistance() {
		return viewdistance;
	}

	public OccupyerPlace[] getOccupyerPlaces() {
		return occupyerplaces.toArray(new OccupyerPlace[occupyerplaces.size()]);
	}

	public RelativePoint[] getAttackers() {
		return attackers.toArray(new RelativePoint[attackers.size()]);
	}
}

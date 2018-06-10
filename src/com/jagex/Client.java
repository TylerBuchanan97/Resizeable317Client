package com.jagex;

import com.tyb.ScreenPolygonGenerator;

import java.applet.AppletContext;
import java.awt.*;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.util.zip.CRC32;

@SuppressWarnings("serial")
public final class Client extends GameApplet {

	// client

	public static int anInt1290;
    public boolean relativeMouse;

	public static int[] BIT_MASKS;
	public static boolean flaggedAccount;
	static boolean displayFps;
	static int drawTick;
	static Player localPlayer;
	static final int[][] PLAYER_BODY_RECOLOURS = { { 6798, 107, 10283, 16, 4797, 7744, 5799, 4634, 33697, 22433, 2983, 54193 },
			{ 8741, 12, 64030, 43162, 7735, 8404, 1701, 38430, 24094, 10153, 56621, 4783, 1341, 16578, 35003, 25239 },
			{ 25238, 8742, 12, 64030, 43162, 7735, 8404, 1701, 38430, 24094, 10153, 56621, 4783, 1341, 16578, 35003 },
			{ 4626, 11146, 6439, 12, 4758, 10270 }, { 4550, 4537, 5681, 5673, 5790, 6806, 8076, 4574 } };
	static int portOffset;
	static final int[] SKIN_COLOURS = { 9104, 10275, 7595, 3610, 7975, 8526, 918, 38802, 24466, 10145, 58654, 5027, 1457, 16565,
			34991, 25486 };
	static int tick;
	private static int anInt1005;
	private static int anInt1051;
	private static int anInt1097;
	private static int anInt1117;
	private static int anInt1134;
	private static int anInt1142;
	private static int anInt1155;
	private static int anInt1175;
	private static int anInt1188;
	private static int anInt1226;
	private static int anInt1288;
	private static int anInt849;
	private static int anInt854;
	private static int anInt924;
	private static int anInt940;
	private static int anInt986;
	private static boolean clientLoaded;
	private static boolean lowMemory;
	private static boolean membersServer = true;
	private static int nodeId = 10;
	private static final BigInteger RSA_EXPONENT = new BigInteger("65537");
	private static final BigInteger RSA_MODULUS = new BigInteger(
			"143690958001225849100503496893758066948984921380482659564113596152800934352119496873386875214251264258425208995167316497331786595942754290983849878549630226741961610780416197036711585670124061149988186026407785250364328460839202438651793652051153157765358767514800252431284681765433239888090564804146588087023");
	private static final int[] SKILL_EXPERIENCE;
	private static final String VALID_INPUT_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!\"\243$%^&*()-_=+[{]};:'@#~,<.>/?\\| ";
	public static final int DEFAULT_WIDTH = 765;
	public static final int DEFAULT_HEIGHT = 503;

	static {
		SKILL_EXPERIENCE = new int[99];
		int value = 0;
		for (int index = 0; index < 99; index++) {
			int level = index + 1;
			value += (int) (level + 300 * Math.pow(2D, level / 7D));
			SKILL_EXPERIENCE[index] = value / 4;
		}

		BIT_MASKS = new int[32];
		value = 2;
		for (int index = 0; index < 32; index++) {
			BIT_MASKS[index] = value - 1;
			value += value;
		}
	}

	public static final String getCombatLevelColour(int user, int opponent) {
		int difference = user - opponent;

		if (difference < -9) {
			return "@red@";
		} else if (difference < -6) {
			return "@or3@";
		} else if (difference < -3) {
			return "@or2@";
		} else if (difference < 0) {
			return "@or1@";
		} else if (difference > 9) {
			return "@gre@";
		} else if (difference > 6) {
			return "@gr3@";
		} else if (difference > 3) {
			return "@gr2@";
		} else if (difference > 0) {
			return "@gr1@";
		}

		return "@yel@";
	}

	public static Client instance;

	public static final void main(String args[]) {

	    if (args.length < 1) {
	        args = new String[] {"0", "0", "highmem", "members", "32"};
        }

		try {
			System.out.println("RS2 user client - release #" + 317);
			if (args.length != 5) {
				System.out.println("Usage: node-id, port-offset, [lowmem/highmem], [free/members], storeid");
				return;
			}

			nodeId = Integer.parseInt(args[0]);
			portOffset = Integer.parseInt(args[1]);
			if (args[2].equals("lowmem")) {
				setLowMemory();
			} else if (args[2].equals("highmem")) {
				setHighMemory();
			} else {
				System.out.println("Usage: node-id, port-offset, [lowmem/highmem], [free/members], storeid");
				return;
			}

			if (args[3].equals("free")) {
				membersServer = false;
			} else if (args[3].equals("members")) {
				membersServer = true;
			} else {
				System.out.println("Usage: node-id, port-offset, [lowmem/highmem], [free/members], storeid");
				return;
			}

			SignLink.storeId = Integer.parseInt(args[4]);
			SignLink.startPriv(InetAddress.getLocalHost());
			instance = new Client();
			instance.initFrame(DEFAULT_HEIGHT, DEFAULT_WIDTH);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static final void setHighMemory() {
		Scene.lowMemory = false;
		Rasterizer.lowMemory = false;
		lowMemory = false;
		Region.lowMemory = false;
		ObjectDefinition.lowMemory = false;
	}

	public static final void setLowMemory() {
		Scene.lowMemory = true;
		Rasterizer.lowMemory = true;
		lowMemory = true;
		Region.lowMemory = true;
		ObjectDefinition.lowMemory = true;
	}

	private static final String getFullAmountText(int amount) {
		String string = String.valueOf(amount);
		for (int index = string.length() - 3; index > 0; index -= 3) {
			string = string.substring(0, index) + "," + string.substring(index);
		}

		if (string.length() > 8) {
			string = "@gre@" + string.substring(0, string.length() - 8) + " million @whi@(" + string + ")";
		} else if (string.length() > 4) {
			string = "@cya@" + string.substring(0, string.length() - 4) + "K @whi@(" + string + ")";
		}

		return " " + string;
	}

	private static final String getShortenedAmountText(int amount) {
		if (amount < 0x186a0) {
			return String.valueOf(amount);
		} else if (amount < 0x989680) {
			return amount / 1000 + "K";
		}

		return amount / 0xf4240 + "M";
	}

	public boolean loggedIn;
	public int[] settings;
	long aLong1220;
	int[] anIntArray840 = new int[1000];
	int duplicateClickCount;
	int lastMouseX;
	int lastMouseY;
	MouseCapturer mouseCapturer;
	int[] npcList = new int[16384];
	int[] playerList = new int[2048];
	Index[] indices;
	String selectedItemName;
	String selectedSpellName;
	boolean wasFocused;
	private boolean aBoolean1017;
	private boolean aBoolean1103;
	private boolean aBoolean1149;
	private boolean redrawChatbox;
	private boolean aBoolean1233;
	private boolean aBoolean1242;
	private boolean redrawBorders;
	private volatile boolean aBoolean831;
	private boolean aBoolean848 = true;
	private boolean aBoolean872;
	private volatile boolean aBoolean880;
	private volatile boolean aBoolean962;
	private boolean aBoolean972;
	private boolean[] aBooleanArray1128;
	private boolean[] aBooleanArray876;
	private byte[] aByteArray912;
	private byte[][][] aByteArrayArrayArray1258;
	private ProducingGraphicsBuffer aClass15_1107;
	private ProducingGraphicsBuffer aClass15_1108;
	private ProducingGraphicsBuffer aClass15_1109;
	private ProducingGraphicsBuffer aClass15_1110;
	private ProducingGraphicsBuffer aClass15_1111;
	private ProducingGraphicsBuffer aClass15_1112;
	private ProducingGraphicsBuffer aClass15_1113;
	private ProducingGraphicsBuffer aClass15_1114;
	private ProducingGraphicsBuffer aClass15_1115;
	private ProducingGraphicsBuffer aClass15_1123;
	private ProducingGraphicsBuffer aClass15_1124;
	private ProducingGraphicsBuffer aClass15_1125;
	private ProducingGraphicsBuffer aClass15_1163;
	private ProducingGraphicsBuffer aClass15_1164;
	private ProducingGraphicsBuffer aClass15_1165;
	private ProducingGraphicsBuffer aClass15_1166;
	private ProducingGraphicsBuffer aClass15_908;
	private ProducingGraphicsBuffer aClass15_909;
	private ProducingGraphicsBuffer aClass15_910;
	private ProducingGraphicsBuffer aClass15_911;
	private DirectSprite aClass30_Sub2_Sub1_Sub1_1201;
	private DirectSprite aClass30_Sub2_Sub1_Sub1_1202;
	private DirectSprite aClass30_Sub2_Sub1_Sub1_1263;
	private DirectSprite aClass30_Sub2_Sub1_Sub1_931;
	private DirectSprite aClass30_Sub2_Sub1_Sub1_932;
	private DirectSprite[] aClass30_Sub2_Sub1_Sub1Array1140;
	private IndexedImage aClass30_Sub2_Sub1_Sub2_1024;
	private IndexedImage aClass30_Sub2_Sub1_Sub2_1025;
	private IndexedImage aClass30_Sub2_Sub1_Sub2_1143;
	private IndexedImage aClass30_Sub2_Sub1_Sub2_1144;
	private IndexedImage aClass30_Sub2_Sub1_Sub2_1145;
	private IndexedImage aClass30_Sub2_Sub1_Sub2_1146;
	private IndexedImage aClass30_Sub2_Sub1_Sub2_1147;
	private IndexedImage aClass30_Sub2_Sub1_Sub2_865;
	private IndexedImage aClass30_Sub2_Sub1_Sub2_866;
	private IndexedImage aClass30_Sub2_Sub1_Sub2_867;
	private IndexedImage aClass30_Sub2_Sub1_Sub2_868;
	private IndexedImage aClass30_Sub2_Sub1_Sub2_869;
	private Font aClass30_Sub2_Sub1_Sub4_1273;
	private Widget aClass9_1059;
	private long aLong1172;
	private long friendHash;
	private int anInt1002;
	private int anInt1010;
	private int anInt1011;
	private int anInt1014;
	private int anInt1015;
	private int anInt1016;
	private int anInt1026;
	private int anInt1039;
	private int anInt1040;
	private int anInt1041;
	private int anInt1048;
	private int anInt1063;
	private int friendListAction;
	private int anInt1066;
	private int anInt1067;
	private int anInt1071;
	private int anInt1079;
	private int anInt1084;
	private int anInt1085;
	private int anInt1086;
	private int anInt1087;
	private int anInt1088;
	private int chatboxScrollerPos;
	private int anInt1098;
	private int anInt1099;
	private int anInt1100;
	private int anInt1101;
	private int anInt1102;
	private int anInt1131;
	private int anInt1132;
	private int anInt1137;
	private int anInt1138;
	private int anInt1170;
	private int anInt1171;
	private int reportInterfaceId;
	private int anInt1186;
	private int anInt1187;
	private int splitPrivateChat;
	private int anInt1209;
	private int anInt1210;
	private int anInt1211 = 78;
	private int anInt1213;
	private int anInt1222;
	private int anInt1243;
	private int anInt1244;
	private int anInt1245;
	private int anInt1246;
	private int chatEffects;
	private int mouseButtons;
	private int anInt1254;
	private int anInt1257;
	private int anInt1264;
	private int anInt1265;
	private int anInt1275;
	private int anInt1278;
	private int anInt1279;
	private int anInt1283;
	private int anInt1284;
	private int anInt1285;
	private int anInt1289;
	private int anInt839;
	private int anInt858;
	private int anInt859;
	private int anInt860;
	private int anInt861;
	private int anInt862;
	private int anInt874;
	private int anInt886;
	private int anInt896;
	private int anInt897;
	private int anInt902 = 0x766654;
	private int bankInsertMode;//check this
	private int anInt914;
	private int anInt915;
	private int anInt916;
	private int anInt917;
	private int anInt927;
	private int anInt934;
	private int anInt935;
	private int anInt936;
	private int anInt937;
	private int anInt938;
	private int anInt948;
	private int anInt949;
	private int anInt950;
	private int anInt951;
	private int anInt952;
	private int textCount;
	private int maxTextCount;
	private int anInt984;
	private int anInt985;
	private int anInt988;
	private int anInt989;
	private int anInt992;
	private int anInt995;
	private int anInt996;
	private int anInt997;
	private int anInt998;
	private int anInt999;
	private int[] anIntArray1030;
	private int[] anIntArray1045;
	private int[] anIntArray1052;
	private int[] anIntArray1057;
	private int[] anIntArray1072;
	private int[] anIntArray1073;
	private int[] anIntArray1091;
	private int[] anIntArray1092;
	private int[] menuActionIds;
	private int[] anIntArray1094;
	private final int[] anIntArray1177 = { 0, 0, 0, 0, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3 };
	private int[] anIntArray1180;
	private int[] anIntArray1181;
	private int[] anIntArray1182;
	private int[] anIntArray1190;
	private int[] anIntArray1191;
	private int[] anIntArray1203;
	private int[] anIntArray1229;
	private int[] anIntArray828;
	private int[] anIntArray829;
	private int[] anIntArray850;
	private int[] anIntArray851;
	private int[] anIntArray852;
	private int[] anIntArray853;
	private int[] anIntArray873;
	private int[] anIntArray928;
	private int[] anIntArray968;
	private int[] anIntArray969;
	private int[] spokenTextX;
	private int[] spokenTextY;
	private int[] spokenTextHeight;
	private int[] spokenTextWidth;
	private int[] spokenTextEffect;
	private int[] spokenTextCycle;
	private int[][] anIntArrayArray825 = new int[104][104];
	private int[][] anIntArrayArray901;
	private int[][] anIntArrayArray929;
	private int[][][] anIntArrayArrayArray1214;
	private int[] archiveCRCs;
	private String textInput;
	private String aString1121;
	private String messagePromptInput;
	private String[] aStringArray1127;
	private String[] spokenText;
	private boolean avatarChanged;;
	private IndexedImage backBase1;
	private IndexedImage backBase2;
	private int backDialogueId;
	private IndexedImage backHmid1;
	private ProducingGraphicsBuffer backLeft1Buffer;
	private ProducingGraphicsBuffer backLeft2Buffer;
	private ProducingGraphicsBuffer backRight1Buffer;
	private ProducingGraphicsBuffer backRight2Buffer;
	private ProducingGraphicsBuffer backTopBuffer;
	private Font boldFont;
	private int cameraRoll;
	private int cameraYaw;
	private int[] characterDesignColours;
	private int[] characterDesignStyles;
	private IndexedImage chatBackground;
	private Buffer chatBuffer = new Buffer(new byte[5000]);
	private String[] chatMessages;
	private String[] chatNames;
	private int[] chatTypes;
	private String clickToContinueString;
	private CollisionMap[] collisionMaps;
	private DirectSprite compass;
	private boolean constructedViewport;
	private DirectSprite[] crosses;
	private int currentStatusInterface;
	private int daysSinceLogin;
	private int daysSinceRecoveryChange;
	private int destinationX;
	private int destinationY;
	private int dialogueId;
	private IsaacCipher encryption;
	private boolean error;
	private boolean fadeMusic;
	private DirectSprite firstMapmarker;
	private int flameTick;
	private int flashingSidebarId;
	private Font frameFont;
	private int friendCount;
	private DirectSprite friendMapdot;
	private long[] friends;
	private int friendServerStatus;
	private String[] friendUsernames;
	private int[] friendWorlds = new int[200];
	private boolean gameAlreadyLoaded;
	private Deque[][][] groundItems = new Deque[4][104][104];
	private int hasMembersCredit;
	private int headIconDrawType;
	private DirectSprite[] headIcons;
	private DirectSprite[] hitMarks;
	private int ignoredCount;
	private long[] ignoredPlayers;
	private Buffer incomingBuffer;
	private Deque incompleteAnimables;
	private CRC32 indexCRC;
	private boolean inPlayerOwnedHouse;
	private String input;
	private int inputDialogueState;
	private int internalLocalPlayerIndex = 2047;
	private IndexedImage inventoryBackground;
	private int inventoryOverlayInterfaceId;
	private int[] inventoryTabIds = { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 };
	private DirectSprite itemMapdot;
	private Socket jaggrabSocket;
	private int lastInteractedWithPlayer;
	private int lastLoginIP;
	private int lastOpcode;
	private int[] levelBase;
	private String loadingScreenText;
	private int loadingStage;
	private long loadingStartTime;
	private int localPlayerIndex;
	private int[] localRegionIds;
	private byte[][] localRegionLandscapeData;
	private int[] localRegionLandscapeIds;
	private byte[][] localRegionMapData;
	private int[] localRegionMapIds;
	private int[][][] localSectors;
	private int localX; // the x co-ordinate relative to the current region
	private int localY; // the y co-ordinate relative to the current region
	private Buffer loginBuffer = Buffer.create();
	private int loginFailures;
	private int loginInputLine;
	private String firstLoginMessage = "";
	private String secondLoginMessage = "";
	private int loginScreenStage;
	private boolean maleAvatar;
	private IndexedImage mapBackground;
	private DirectSprite mapEdge;
	private DirectSprite[] mapFunctions;
	private IndexedImage[] mapScenes;
	private int maximumPlayers = 2048;
	private int member;
	private int menuActionRow;
	private String[] menuActionTexts;
	private boolean menuOpen;
	private boolean messagePromptRaised;
	private int minimapState;
	private int[] mobsAwaitingUpdate;
	private int mobsAwaitingUpdateCount;
	private IndexedImage[] modIcons = new IndexedImage[2];
	private int multicombat;
	private int musicId;
	private int nextMusicId;
	private int npcCount;
	private DirectSprite npcMapdot;
	private Npc[] npcs = new Npc[16384];
	private int onTutorialIsland;
	private int opcode;
	private int openInterfaceId;
	private boolean oriented;
	private Buffer outgoing;;
	private int packetSize;
	private String password = "testing";
	private int plane;
	private int playerCount;
	private DirectSprite playerMapdot;
	private int playerPrivelage;
	private Player[] players = new Player[maximumPlayers];
	private Buffer[] playerSynchronizationBuffers;
	private boolean playingMusic;
	private int previousAbsoluteX;
	private int previousAbsoluteY;
	private BufferedConnection primary;
	private int privateChatMode;
	private int privateMessageCount;
	private int[] privateMessageIds;
	private Deque projectiles;
	private ResourceProvider provider;
	private int publicChatMode;
	private boolean redrawTabArea;
	private int regionBaseX;
	private int regionBaseY;
	private boolean reportAbuseMuteToggle;
	private String reportInput;
	private int runEnergy;
	private IndexedImage[] runes;
	private Scene scene;
	private int secondLastOpcode;
	private DirectSprite secondMapmarker;
	private int sectorX;
	private int sectorY;
	private int selectedItemId;
	private int selectedSpellId;
	private long serverSeed;
	private IndexedImage[] sideIcons;
	private int[] skillExperience;
	private int[] skillLevel;
	private Font smallFont;
	private int songDelay;
	private Deque spawns;
	private int spriteDrawX;
	private int spriteDrawY;
	private int systemUpdateTime;
	private int tabId;
	private DirectSprite teamMapdot;
	private int[] spokenTextColour;
	private int[] textColours = { 0xffff00, 0xff0000, 65280, 65535, 0xff00ff, 0xffffff };
	private int thirdLastOpcode;
	private int tickDelta;
	private int timeoutCounter;
	private IndexedImage titleBox;
	private IndexedImage titleButton;
	private Archive titleScreen;
	private int trackCount;
	private int[] trackDelays = new int[50];
	private int[] trackLoops = new int[50];
	private int[] tracks;
	private int tradeChatMode;
	private boolean unableToLoad;
	private int unreadMessageCount;
	private String username;
	private boolean validLocalMap;
	private int[] waypointX = new int[4000];
	private int[] waypointY = new int[4000];
	private int weight;

	public Client() {
		openInterfaceId = -1;
		skillExperience = new int[SkillConstants.SKILL_COUNT];
		anIntArray873 = new int[5];
		anInt874 = -1;
		aBooleanArray876 = new boolean[5];
		reportInput = "";
		localPlayerIndex = -1;
		input = "";
		mobsAwaitingUpdate = new int[maximumPlayers];
		playerSynchronizationBuffers = new Buffer[maximumPlayers];
		anInt897 = 1;
		anIntArrayArray901 = new int[104][104];
		aByteArray912 = new byte[16384];
		skillLevel = new int[SkillConstants.SKILL_COUNT];
		ignoredPlayers = new long[100];
		anInt927 = 0x332d25;
		anIntArray928 = new int[5];
		anIntArrayArray929 = new int[104][104];
		indexCRC = new CRC32();
		chatTypes = new int[100];
		chatNames = new String[100];
		chatMessages = new String[100];
		sideIcons = new IndexedImage[13];
		wasFocused = true;
		friends = new long[200];
		nextMusicId = -1;
		spriteDrawX = -1;
		spriteDrawY = -1;
		anIntArray968 = new int[33];
		anIntArray969 = new int[256];
		indices = new Index[5];
		settings = new int[2000];
		maxTextCount = 50;
		spokenTextX = new int[maxTextCount];
		spokenTextY = new int[maxTextCount];
		spokenTextHeight = new int[maxTextCount];
		spokenTextWidth = new int[maxTextCount];
		spokenTextColour = new int[maxTextCount];
		spokenTextEffect = new int[maxTextCount];
		spokenTextCycle = new int[maxTextCount];
		spokenText = new String[maxTextCount];
		anInt985 = -1;
		hitMarks = new DirectSprite[20];
		characterDesignColours = new int[5];
		anInt1002 = 0x23201b;
		textInput = "";
		projectiles = new Deque();
		currentStatusInterface = -1;
		anIntArray1030 = new int[5];
		mapFunctions = new DirectSprite[100];
		dialogueId = -1;
		levelBase = new int[SkillConstants.SKILL_COUNT];
		anIntArray1045 = new int[2000];
		maleAvatar = true;
		anIntArray1052 = new int[151];
		flashingSidebarId = -1;
		incompleteAnimables = new Deque();
		anIntArray1057 = new int[33];
		aClass9_1059 = new Widget();
		mapScenes = new IndexedImage[100];
		anInt1063 = 0x4d4233;
		characterDesignStyles = new int[7];
		anIntArray1072 = new int[1000];
		anIntArray1073 = new int[1000];
		friendUsernames = new String[200];
		incomingBuffer = Buffer.create();
		archiveCRCs = new int[9];
		anIntArray1091 = new int[500];
		anIntArray1092 = new int[500];
		menuActionIds = new int[500];
		anIntArray1094 = new int[500];
		headIcons = new DirectSprite[20];
		aString1121 = "";
		aStringArray1127 = new String[5];
		aBooleanArray1128 = new boolean[5];
		localSectors = new int[4][13][13];
		anInt1132 = 2;
		aClass30_Sub2_Sub1_Sub1Array1140 = new DirectSprite[1000];
		crosses = new DirectSprite[8];
		playingMusic = true;
		loggedIn = false;
		anInt1171 = 1;
		reportInterfaceId = -1;
		spawns = new Deque();
		cameraRoll = 128;
		inventoryOverlayInterfaceId = -1;
		outgoing = Buffer.create();
		menuActionTexts = new String[500];
		anIntArray1203 = new int[5];
		tracks = new int[50];
		anInt1210 = 2;
		messagePromptInput = "";
		modIcons = new IndexedImage[2];
		tabId = 3;
		fadeMusic = true;
		anIntArray1229 = new int[151];
		collisionMaps = new CollisionMap[4];
		privateMessageIds = new int[100];
		backDialogueId = -1;
		anInt1279 = 2;
		anInt1289 = -1;
	}

	public final void addChatMessage(int type, String message, String name) {
		if (type == 0 && dialogueId != -1) {
			clickToContinueString = message;
			super.lastMetaModifier = 0;
		}

		if (backDialogueId == -1) {
			redrawChatbox = true;
		}

		for (int index = 99; index > 0; index--) {
			chatTypes[index] = chatTypes[index - 1];
			chatNames[index] = chatNames[index - 1];
			chatMessages[index] = chatMessages[index - 1];
		}

		chatTypes[0] = type;
		chatNames[0] = name;
		chatMessages[0] = message;
	}

	public final void addFriend(long name) {
		if (name == 0) {
			return;
		}

		if (friendCount >= 100 && member != 1) {
			addChatMessage(0, "Your friendlist is full. Max of 100 for free users, and 200 for members", "");
			return;
		} else if (friendCount >= 200) {
			addChatMessage(0, "Your friendlist is full. Max of 100 for free users, and 200 for members", "");
			return;
		}

		String username = StringUtils.format(StringUtils.decodeBase37(name));
		for (int index = 0; index < friendCount; index++) {
			if (friends[index] == name) {
				addChatMessage(0, username + " is already on your friend list", "");
				return;
			}
		}

		for (int index = 0; index < ignoredCount; index++) {
			if (ignoredPlayers[index] == name) {
				addChatMessage(0, "Please remove " + username + " from your ignore list first", "");
				return;
			}
		}

		if (username.equals(localPlayer.name)) {
			return;
		}
		friendUsernames[friendCount] = username;
		friends[friendCount] = name;
		friendWorlds[friendCount] = 0;
		friendCount++;
		redrawTabArea = true;
		outgoing.writeOpcode(188);
		outgoing.writeLong(name);
	}

	public final void adjustMidiVolume(boolean flag, int volume) {
		SignLink.midiVolume = volume;

		if (flag) {
			SignLink.midi = "voladjust";
		}
	}

	public final void attemptReconnection() {
		if (anInt1011 > 0) {
			reset();
			return;
		}

		aClass15_1165.initializeRasterizer();
		frameFont.renderCentre(257, 144, "Connection lost", 0);
		frameFont.renderCentre(256, 143, "Connection lost", 0xffffff);
		frameFont.renderCentre(257, 159, "Please wait - attempting to reestablish", 0);
		frameFont.renderCentre(256, 158, "Please wait - attempting to reestablish", 0xffffff);
		aClass15_1165.drawGameImage(super.graphics, 4, 4);
		minimapState = 0;
		destinationX = 0;
		BufferedConnection connection = primary;
		loggedIn = false;
		loginFailures = 0;
		login(username, password, true);

		if (!loggedIn) {
			reset();
		}

		try {
			connection.stop();
		} catch (Exception ex) {
		}
	}

	public final void changeCharacterGender() {
		avatarChanged = true;
		for (int part = 0; part < 7; part++) {
			characterDesignStyles[part] = -1;

			for (int kit = 0; kit < IdentityKit.count; kit++) {
				if (IdentityKit.kits[kit].validStyle || IdentityKit.kits[kit].part != part + (maleAvatar ? 0 : 7)) {
					continue;
				}

				characterDesignStyles[part] = kit;
				break;
			}
		}
	}

	public final void checkTutorialIsland() {
		onTutorialIsland = 0;
		int x = (localPlayer.worldX >> 7) + regionBaseX;
		int y = (localPlayer.worldY >> 7) + regionBaseY;
		if (x >= 3053 && x <= 3156 && y >= 3056 && y <= 3136) {
			onTutorialIsland = 1;
		}
		if (x >= 3072 && x <= 3118 && y >= 9492 && y <= 9535) {
			onTutorialIsland = 1;
		}
		if (onTutorialIsland == 1 && x >= 3139 && x <= 3199 && y >= 3008 && y <= 3062) {
			onTutorialIsland = 0;
		}
	}

	public final void clearTopInterfaces() {
		outgoing.writeOpcode(130);

		if (inventoryOverlayInterfaceId != -1) {
			inventoryOverlayInterfaceId = -1;
			redrawTabArea = true;
			aBoolean1149 = false;
			aBoolean1103 = true;
		}

		if (backDialogueId != -1) {
			backDialogueId = -1;
			redrawChatbox = true;
			aBoolean1149 = false;
		}

		openInterfaceId = -1;
	}

	public final Archive createArchive(int index, String displayedName, String name, int expectedCRC, int k) {
		byte[] archiveBuffer = null;
		int reconnectionDelay = 5;

		try {
			if (indices[0] != null) {
				archiveBuffer = indices[0].decompress(index);
			}
		} catch (Exception ex) {
		}

		if (archiveBuffer != null) {
			indexCRC.reset();
			indexCRC.update(archiveBuffer);
			int crc = (int) indexCRC.getValue();
			if (crc != expectedCRC) {
				archiveBuffer = null;
			}
		}

		if (archiveBuffer != null) {
			return new Archive(archiveBuffer);
		}

		int errors = 0;
		while (archiveBuffer == null) {
			String message = "Unknown error";
			drawLoadingText(k, "Requesting " + displayedName);
			try {
				int last = 0;
				DataInputStream in = requestCacheIndex(name + expectedCRC);
				byte[] buf = new byte[6];
				in.readFully(buf, 0, 6);
				Buffer buffer = new Buffer(buf);
				buffer.position = 3;
				int size = buffer.readTriByte() + 6;
				int offset = 6;
				archiveBuffer = new byte[size];

				for (int i = 0; i < 6; i++) {
					archiveBuffer[i] = buf[i];
				}

				while (offset < size) {
					int length = size - offset;
					if (length > 1000) {
						length = 1000;
					}
					int read = in.read(archiveBuffer, offset, length);
					if (read < 0) {
						message = "Length error: " + offset + "/" + size;
						throw new IOException("EOF");
					}
					offset += read;
					int completed = offset * 100 / size;
					if (completed != last) {
						drawLoadingText(k, "Loading " + displayedName + " - " + completed + "%");
					}
					last = completed;
				}
				in.close();
				try {
					if (indices[0] != null) {
						indices[0].put(archiveBuffer, index, archiveBuffer.length);
					}
				} catch (Exception _ex) {
					indices[0] = null;
				}
				indexCRC.reset();
				indexCRC.update(archiveBuffer);
				int crc = (int) indexCRC.getValue();
				if (crc != expectedCRC) {
					archiveBuffer = null;
					errors++;
					message = "Checksum error: " + crc;
				}
			} catch (IOException ex) {
				if (message.equals("Unknown error")) {
					message = "Connection error";
				}
				archiveBuffer = null;
			} catch (NullPointerException ex) {
				message = "Null error";
				archiveBuffer = null;
				if (!SignLink.reportError) {
					return null;
				}
			} catch (ArrayIndexOutOfBoundsException ex) {
				message = "Bounds error";
				archiveBuffer = null;
				if (!SignLink.reportError) {
					return null;
				}
			} catch (Exception ex) {
				message = "Unexpected error";
				archiveBuffer = null;
				if (!SignLink.reportError) {
					return null;
				}
			}

			if (archiveBuffer == null) {
				for (int seconds = reconnectionDelay; seconds > 0; seconds--) {
					if (errors >= 3) {
						drawLoadingText(k, "Game updated - please reload page");
						seconds = 10;
					} else {
						drawLoadingText(k, message + " - Retrying in " + seconds);
					}
					try {
						Thread.sleep(1000L);
					} catch (Exception ex) {
					}
				}

				reconnectionDelay *= 2;
				if (reconnectionDelay > 60) {
					reconnectionDelay = 60;
				}

				aBoolean872 = !aBoolean872;
			}
		}

		return new Archive(archiveBuffer);
	}

	public void debug() {
		System.out.println("============");
		System.out.println("flame-cycle:" + flameTick);
		if (provider != null) {
			System.out.println("Od-cycle:" + provider.tick);
		}

		System.out.println("loop-cycle:" + tick);
		System.out.println("draw-cycle:" + drawTick);
		System.out.println("ptype:" + opcode);
		System.out.println("psize:" + packetSize);

		if (primary != null) {
			primary.debug();
		}

		super.debug = true;
	}

	public final void displayErrorMessage() {
		Graphics graphics = getFrame().getGraphics();
		graphics.setColor(Color.black);
		graphics.fillRect(0, 0, 765, 503);
		resetTimeDelta();

		if (error) {
			aBoolean831 = false;
			graphics.setFont(new java.awt.Font("Helvetica", 1, 16));
			graphics.setColor(Color.yellow);
			int y = 35;
			graphics.drawString("Sorry, an error has occured whilst loading RuneScape", 30, y);
			y += 50;
			graphics.setColor(Color.white);
			graphics.drawString("To fix this try the following (in order):", 30, y);
			y += 50;
			graphics.setColor(Color.white);
			graphics.setFont(new java.awt.Font("Helvetica", 1, 12));
			graphics.drawString("1: Try closing ALL open web-browser windows, and reloading", 30, y);
			y += 30;
			graphics.drawString("2: Try clearing your web-browsers cache from tools->internet options", 30, y);
			y += 30;
			graphics.drawString("3: Try using a different game-world", 30, y);
			y += 30;
			graphics.drawString("4: Try rebooting your computer", 30, y);
			y += 30;
			graphics.drawString("5: Try selecting a different version of Java from the play-game menu", 30, y);
		}

		if (unableToLoad) {
			aBoolean831 = false;
			graphics.setFont(new java.awt.Font("Helvetica", 1, 20));
			graphics.setColor(Color.white);
			graphics.drawString("Error - unable to load game!", 50, 50);
			graphics.drawString("To play RuneScape make sure you play from", 50, 100);
			graphics.drawString("http://www.runescape.com", 50, 150);
		}

		if (gameAlreadyLoaded) {
			aBoolean831 = false;
			graphics.setColor(Color.yellow);
			int y = 35;
			graphics.drawString("Error a copy of RuneScape already appears to be loaded", 30, y);
			y += 50;
			graphics.setColor(Color.white);
			graphics.drawString("To fix this try the following (in order):", 30, y);
			y += 50;
			graphics.setColor(Color.white);
			graphics.setFont(new java.awt.Font("Helvetica", 1, 12));
			graphics.drawString("1: Try closing ALL open web-browser windows, and reloading", 30, y);
			y += 30;
			graphics.drawString("2: Try rebooting your computer, and reloading", 30, y);
			y += 30;
		}
	}

	@Override
	public final void draw() {
		if (gameAlreadyLoaded || error || unableToLoad) {
			displayErrorMessage();
			return;
		}
		drawTick++;

		if (!loggedIn) {
			drawLoginScreen(false);
		} else {
			drawGameScreen();
		}
		anInt1213 = 0;
	}

	public final void drawChatMessages() {
		if (splitPrivateChat == 0) {
			return;
		}
		Font font = frameFont;
		int message = 0;
		if (systemUpdateTime != 0) {
			message = 1;
		}

		for (int index = 0; index < 100; index++) {
			if (chatMessages[index] != null) {
				int type = chatTypes[index];
				String name = chatNames[index];
				byte privilege = 0;

				if (name != null && name.startsWith("@cr1@")) {
					name = name.substring(5);
					privilege = 1;
				} else if (name != null && name.startsWith("@cr2@")) {
					name = name.substring(5);
					privilege = 2;
				}

				if ((type == 3 || type == 7)
						&& (type == 7 || privateChatMode == 0 || privateChatMode == 1 && displayMessageFrom(name))) {
					int y = 329 - message * 13;
					int x = 4;
					font.render(x, y, "From", 0);
					font.render(x, y - 1, "From", 65535);
					x += font.getTextWidth("From ");

					if (privilege == 1) {
						modIcons[0].draw(x, y - 12);
						x += 14;
					} else if (privilege == 2) {
						modIcons[1].draw(x, y - 12);
						x += 14;
					}

					font.render(x, y, name + ": " + chatMessages[index], 0);
					font.render(x, y - 1, name + ": " + chatMessages[index], 65535);
					if (++message >= 5) {
						return;
					}
				}

				if (type == 5 && privateChatMode < 2) {
					int y = 329 - message * 13;
					font.render(4, y, chatMessages[index], 0);
					font.render(4, y - 1, chatMessages[index], 65535);
					if (++message >= 5) {
						return;
					}
				} else if (type == 6 && privateChatMode < 2) {
					int y = 329 - message * 13;
					font.render(4, y, "To " + name + ": " + chatMessages[index], 0);
					font.render(4, y - 1, "To " + name + ": " + chatMessages[index], 65535);
					if (++message >= 5) {
						return;
					}
				}
			}
		}
	}

    boolean windowResized;

    public final void drawGameScreen() {
		if (redrawBorders) {
			redrawBorders = false;
			backLeft1Buffer.drawImage(super.graphics, 0, 4);
			backLeft2Buffer.drawImage(super.graphics, 0, 357);
			backRight1Buffer.drawImage(super.graphics, 722, 4);
			backRight2Buffer.drawImage(super.graphics, 743, 205);
			backTopBuffer.drawImage(super.graphics, 0, 0);
			aClass15_908.drawImage(super.graphics, 516, 4);
			aClass15_909.drawImage(super.graphics, 516, 205);
			aClass15_910.drawImage(super.graphics, 496, 357);
			aClass15_911.drawImage(super.graphics, 0, 338);//Top of the chat box?
			redrawTabArea = true;
			redrawChatbox = true;
			aBoolean1103 = true;
			aBoolean1233 = true;
			if (loadingStage != 2) {
				aClass15_1165.drawGameImage(super.graphics, 4, 4);
				aClass15_1164.drawImage(super.graphics, 550, 4);
			}
		}

		if (loadingStage == 2) {
		    try {
                if (windowResized) {
                    windowResized = false;
                    if (frameHeight > Client.DEFAULT_HEIGHT || frameWidth > Client.DEFAULT_WIDTH) {
                        aClass15_1165 = new ProducingGraphicsBuffer(getFrame(), frameWidth, frameHeight);

                        Rasterizer.method365(frameWidth, frameHeight);
                        anIntArray1182 = Rasterizer.scanOffsets;
                        int ai[] = new int[9];
                        for (int i8 = 0; i8 < 9; i8++) {
                            int theta = 128 + i8 * 32 + 15;
                            int l8 = 600 + theta * 3;
                            int i9 = Rasterizer.SINE[theta];
                            ai[i8] = l8 * i9 >> 16;
                        }
                        Scene.method310(500, 800, frameWidth, frameHeight, ai);
                    } else {
                        aClass15_1165 = new ProducingGraphicsBuffer(getFrame(), 512, 334);

                        Rasterizer.method365(512, 334);
                        anIntArray1182 = Rasterizer.scanOffsets;
                        int ai[] = new int[9];
                        for (int i8 = 0; i8 < 9; i8++) {
                            int theta = 128 + i8 * 32 + 15;
                            int l8 = 600 + theta * 3;
                            int i9 = Rasterizer.SINE[theta];
                            ai[i8] = l8 * i9 >> 16;
                        }
                        Scene.method310(500, 800, 512, 334, ai);
                    }
                }

                method146();
            } catch (ArrayIndexOutOfBoundsException ex) {
		        //Just going to let it ride, might be slow to update.
                ex.printStackTrace();
            }
		}

		if (menuOpen && anInt948 == 1) {
			redrawTabArea = true;
		}

		if (inventoryOverlayInterfaceId != -1) {
			boolean redrawRequired = processWidgetAnimations(inventoryOverlayInterfaceId, tickDelta);
			if (redrawRequired) {
				redrawTabArea = true;
			}
		}
		if (anInt1246 == 2) {
			redrawTabArea = true;
		}
		if (anInt1086 == 2) {
			redrawTabArea = true;
		}
		if (redrawTabArea) {
			method36();
			redrawTabArea = false;
		}
		if (backDialogueId == -1) {
			aClass9_1059.scrollPosition = anInt1211 - chatboxScrollerPos - 77;
			if (super.mouseEventX > 448 && super.mouseEventX < 560 && super.mouseEventY > 332) {
				method65(463, 77, super.mouseEventX - 17, super.mouseEventY - 357, aClass9_1059, 0, false, anInt1211, 0);
			}
			int i = anInt1211 - 77 - aClass9_1059.scrollPosition;
			if (i < 0) {
				i = 0;
			}
			if (i > anInt1211 - 77) {
				i = anInt1211 - 77;
			}
			if (chatboxScrollerPos != i) {
				chatboxScrollerPos = i;
				redrawChatbox = true;
			}
		}
		if (backDialogueId != -1) {
			boolean flag2 = processWidgetAnimations(backDialogueId, tickDelta);
			if (flag2) {
				redrawChatbox = true;
			}
		}
		if (anInt1246 == 3) {
			redrawChatbox = true;
		}
		if (anInt1086 == 3) {
			redrawChatbox = true;
		}
		if (clickToContinueString != null) {
			redrawChatbox = true;
		}
		if (menuOpen && anInt948 == 2) {
			redrawChatbox = true;
		}
		if (redrawChatbox) {
			method18();
			redrawChatbox = false;
		}
		if (loadingStage == 2) {
			method126();
			aClass15_1164.drawImage(super.graphics, 550, 4);
		}
		if (flashingSidebarId != -1) {
			aBoolean1103 = true;
		}
		if (aBoolean1103) {
			if (flashingSidebarId != -1 && flashingSidebarId == tabId) {
				flashingSidebarId = -1;
				outgoing.writeOpcode(120);
				outgoing.writeByte(tabId);
			}
			aBoolean1103 = false;
			aClass15_1125.initializeRasterizer();
			backHmid1.draw(0, 0);
			if (inventoryOverlayInterfaceId == -1) {
				if (inventoryTabIds[tabId] != -1) {
					if (tabId == 0) {
						aClass30_Sub2_Sub1_Sub2_1143.draw(22, 10);
					}
					if (tabId == 1) {
						aClass30_Sub2_Sub1_Sub2_1144.draw(54, 8);
					}
					if (tabId == 2) {
						aClass30_Sub2_Sub1_Sub2_1144.draw(82, 8);
					}
					if (tabId == 3) {
						aClass30_Sub2_Sub1_Sub2_1145.draw(110, 8);
					}
					if (tabId == 4) {
						aClass30_Sub2_Sub1_Sub2_1147.draw(153, 8);
					}
					if (tabId == 5) {
						aClass30_Sub2_Sub1_Sub2_1147.draw(181, 8);
					}
					if (tabId == 6) {
						aClass30_Sub2_Sub1_Sub2_1146.draw(209, 9);
					}
				}
				if (inventoryTabIds[0] != -1 && (flashingSidebarId != 0 || tick % 20 < 10)) {
					sideIcons[0].draw(29, 13);
				}
				if (inventoryTabIds[1] != -1 && (flashingSidebarId != 1 || tick % 20 < 10)) {
					sideIcons[1].draw(53, 11);
				}
				if (inventoryTabIds[2] != -1 && (flashingSidebarId != 2 || tick % 20 < 10)) {
					sideIcons[2].draw(82, 11);
				}
				if (inventoryTabIds[3] != -1 && (flashingSidebarId != 3 || tick % 20 < 10)) {
					sideIcons[3].draw(115, 12);
				}
				if (inventoryTabIds[4] != -1 && (flashingSidebarId != 4 || tick % 20 < 10)) {
					sideIcons[4].draw(153, 13);
				}
				if (inventoryTabIds[5] != -1 && (flashingSidebarId != 5 || tick % 20 < 10)) {
					sideIcons[5].draw(180, 11);
				}
				if (inventoryTabIds[6] != -1 && (flashingSidebarId != 6 || tick % 20 < 10)) {
					sideIcons[6].draw(208, 13);
				}
			}
			aClass15_1125.drawImage(super.graphics, 516, 160);
			aClass15_1124.initializeRasterizer();
			backBase2.draw(0, 0);
			if (inventoryOverlayInterfaceId == -1) {
				if (inventoryTabIds[tabId] != -1) {
					if (tabId == 7) {
						aClass30_Sub2_Sub1_Sub2_865.draw(42, 0);
					}
					if (tabId == 8) {
						aClass30_Sub2_Sub1_Sub2_866.draw(74, 0);
					}
					if (tabId == 9) {
						aClass30_Sub2_Sub1_Sub2_866.draw(102, 0);
					}
					if (tabId == 10) {
						aClass30_Sub2_Sub1_Sub2_867.draw(130, 1);
					}
					if (tabId == 11) {
						aClass30_Sub2_Sub1_Sub2_869.draw(173, 0);
					}
					if (tabId == 12) {
						aClass30_Sub2_Sub1_Sub2_869.draw(201, 0);
					}
					if (tabId == 13) {
						aClass30_Sub2_Sub1_Sub2_868.draw(229, 0);
					}
				}
				if (inventoryTabIds[8] != -1 && (flashingSidebarId != 8 || tick % 20 < 10)) {
					sideIcons[7].draw(74, 2);
				}
				if (inventoryTabIds[9] != -1 && (flashingSidebarId != 9 || tick % 20 < 10)) {
					sideIcons[8].draw(102, 3);
				}
				if (inventoryTabIds[10] != -1 && (flashingSidebarId != 10 || tick % 20 < 10)) {
					sideIcons[9].draw(137, 4);
				}
				if (inventoryTabIds[11] != -1 && (flashingSidebarId != 11 || tick % 20 < 10)) {
					sideIcons[10].draw(174, 2);
				}
				if (inventoryTabIds[12] != -1 && (flashingSidebarId != 12 || tick % 20 < 10)) {
					sideIcons[11].draw(201, 2);
				}
				if (inventoryTabIds[13] != -1 && (flashingSidebarId != 13 || tick % 20 < 10)) {
					sideIcons[12].draw(226, 2);
				}
			}
			aClass15_1124.drawImage(super.graphics, 496, 466);
			aClass15_1165.initializeRasterizer();
		}
		if (aBoolean1233) {
			aBoolean1233 = false;
			aClass15_1123.initializeRasterizer();
			backBase1.draw(0, 0);
			frameFont.shadowCentre(55, 28, "Public chat", true, 0xffffff);
			if (publicChatMode == 0) {
				frameFont.shadowCentre(55, 41, "On", true, 65280);
			}
			if (publicChatMode == 1) {
				frameFont.shadowCentre(55, 41, "Friends", true, 0xffff00);
			}
			if (publicChatMode == 2) {
				frameFont.shadowCentre(55, 41, "Off", true, 0xff0000);
			}
			if (publicChatMode == 3) {
				frameFont.shadowCentre(55, 41, "Hide", true, 65535);
			}
			frameFont.shadowCentre(184, 28, "Private chat", true, 0xffffff);
			if (privateChatMode == 0) {
				frameFont.shadowCentre(184, 41, "On", true, 65280);
			}
			if (privateChatMode == 1) {
				frameFont.shadowCentre(184, 41, "Friends", true, 0xffff00);
			}
			if (privateChatMode == 2) {
				frameFont.shadowCentre(184, 41, "Off", true, 0xff0000);
			}
			frameFont.shadowCentre(324, 28, "Trade/compete", true, 0xffffff);
			if (tradeChatMode == 0) {
				frameFont.shadowCentre(324, 41, "On", true, 65280);
			}
			if (tradeChatMode == 1) {
				frameFont.shadowCentre(324, 41, "Friends", true, 0xffff00);
			}
			if (tradeChatMode == 2) {
				frameFont.shadowCentre(324, 41, "Off", true, 0xff0000);
			}
			frameFont.shadowCentre(458, 33, "Report abuse", true, 0xffffff);
			aClass15_1123.drawImage(super.graphics, 0, 453);
			aClass15_1165.initializeRasterizer();
		}
		tickDelta = 0;
	}

    @Override
	public final void drawLoadingText(int i, String text) {
		anInt1079 = i;
		loadingScreenText = text;
		method64();
		if (titleScreen == null) {
			super.drawLoadingText(i, text);
			return;
		}
		aClass15_1109.initializeRasterizer();
		char x = '\u0168';
		char c1 = '\310';
		byte byte1 = 20;
		boldFont.renderCentre(x / 2, c1 / 2 - 26 - byte1, "RuneScape is loading - please wait...", 0xffffff);
		int y = c1 / 2 - 18 - byte1;
		Raster.drawRectangle(x / 2 - 152, y, 304, 34, 0x8c1111);
		Raster.drawRectangle(x / 2 - 151, y + 1, 302, 32, 0);
		Raster.fillRectangle(x / 2 - 150, y + 2, 30, i * 3, 0x8c1111);
		Raster.fillRectangle(x / 2 - 150 + i * 3, y + 2, 30, 300 - i * 3, 0);
		boldFont.renderCentre(x / 2, c1 / 2 + 5 - byte1, text, 0xffffff);
		aClass15_1109.drawImage(super.graphics, 202, 171);
		if (redrawBorders) {
			redrawBorders = false;
			if (!aBoolean831) {
				aClass15_1110.drawImage(super.graphics, 0, 0);
				aClass15_1111.drawImage(super.graphics, 637, 0);
			}
			aClass15_1107.drawImage(super.graphics, 128, 0);
			aClass15_1108.drawImage(super.graphics, 202, 371);
			aClass15_1112.drawImage(super.graphics, 0, 265);
			aClass15_1113.drawImage(super.graphics, 562, 265);
			aClass15_1114.drawImage(super.graphics, 128, 171);
			aClass15_1115.drawImage(super.graphics, 562, 171);
		}
	}

	public final void drawLoginScreen(boolean flag) {
		method64();
		aClass15_1109.initializeRasterizer();
		titleBox.draw(0, 0);
		char x = '\u0168';
		char c1 = '\310';
		if (loginScreenStage == 0) {
			int y = c1 / 2 + 80;
			smallFont.shadowCentre(x / 2, y, provider.loadingMessage, true, 0x75a9a9);
			y = c1 / 2 - 20;
			boldFont.shadowCentre(x / 2, y, "Welcome to RuneScape", true, 0xffff00);
			y += 30;
			int buttonX = x / 2 - 80;
			int buttonY = c1 / 2 + 20;
			titleButton.draw(buttonX - 73, buttonY - 20);
			boldFont.shadowCentre(buttonX, buttonY + 5, "New User", true, 0xffffff);
			buttonX = x / 2 + 80;
			titleButton.draw(buttonX - 73, buttonY - 20);
			boldFont.shadowCentre(buttonX, buttonY + 5, "Existing User", true, 0xffffff);
		} else if (loginScreenStage == 2) {
			int y = c1 / 2 - 40;
			if (firstLoginMessage.length() > 0) {
				boldFont.shadowCentre(x / 2, y - 15, firstLoginMessage, true, 0xffff00);
				boldFont.shadowCentre(x / 2, y, secondLoginMessage, true, 0xffff00);
				y += 30;
			} else {
				boldFont.shadowCentre(x / 2, y - 7, secondLoginMessage, true, 0xffff00);
				y += 30;
			}
			boldFont.shadow(x / 2 - 90, y, "Username: " + username + (loginInputLine == 0 & tick % 40 < 20 ? "@yel@|" : ""),
					true, 0xffffff);
			y += 15;
			boldFont.shadow(x / 2 - 88, y, "Password: " + StringUtils.getAsterisks(password)
					+ (loginInputLine == 1 & tick % 40 < 20 ? "@yel@|" : ""), true, 0xffffff);
			y += 15;
			if (!flag) {
				int cancelX = x / 2 - 80;
				int cancelY = c1 / 2 + 50;
				titleButton.draw(cancelX - 73, cancelY - 20);
				boldFont.shadowCentre(cancelX, cancelY + 5, "Login", true, 0xffffff);
				cancelX = x / 2 + 80;
				titleButton.draw(cancelX - 73, cancelY - 20);
				boldFont.shadowCentre(cancelX, cancelY + 5, "Cancel", true, 0xffffff);
			}
		} else if (loginScreenStage == 3) {
			boldFont.shadowCentre(x / 2, c1 / 2 - 60, "Create a free account", true, 0xffff00);
			int y = c1 / 2 - 35;
			boldFont.shadowCentre(x / 2, y, "To create a new account you need to", true, 0xffffff);
			y += 15;
			boldFont.shadowCentre(x / 2, y, "go back to the main RuneScape webpage", true, 0xffffff);
			y += 15;
			boldFont.shadowCentre(x / 2, y, "and choose the red 'create account'", true, 0xffffff);
			y += 15;
			boldFont.shadowCentre(x / 2, y, "button at the top right of that page.", true, 0xffffff);
			y += 15;
			int cancelX = x / 2;
			int cancelY = c1 / 2 + 50;
			titleButton.draw(cancelX - 73, cancelY - 20);
			boldFont.shadowCentre(cancelX, cancelY + 5, "Cancel", true, 0xffffff);
		}

		aClass15_1109.drawImage(super.graphics, 202, 171);
		if (redrawBorders) {
			redrawBorders = false;
			aClass15_1107.drawImage(super.graphics, 128, 0);
			aClass15_1108.drawImage(super.graphics, 202, 371);
			aClass15_1112.drawImage(super.graphics, 0, 265);
			aClass15_1113.drawImage(super.graphics, 562, 265);
			aClass15_1114.drawImage(super.graphics, 128, 171);
			aClass15_1115.drawImage(super.graphics, 562, 171);
		}
	}

	public final void drawScreen() {
		if (aClass15_1166 != null) {
			return;
		}
		method118();
		super.frameGraphicsBuffer = null;
		aClass15_1107 = null;
		aClass15_1108 = null;
		aClass15_1109 = null;
		aClass15_1110 = null;
		aClass15_1111 = null;
		aClass15_1112 = null;
		aClass15_1113 = null;
		aClass15_1114 = null;
		aClass15_1115 = null;
		aClass15_1166 = new ProducingGraphicsBuffer(getFrame(), 479, 96);
		aClass15_1164 = new ProducingGraphicsBuffer(getFrame(), 172, 156);
		Raster.reset();
		mapBackground.draw(0, 0);
		aClass15_1163 = new ProducingGraphicsBuffer(getFrame(), 190, 261);
		aClass15_1165 = new ProducingGraphicsBuffer(getFrame(), 512, 334);
		Raster.reset();
		aClass15_1123 = new ProducingGraphicsBuffer(getFrame(), 496, 50);
		aClass15_1124 = new ProducingGraphicsBuffer(getFrame(), 269, 37);
		aClass15_1125 = new ProducingGraphicsBuffer(getFrame(), 249, 45);
		redrawBorders = true;
	}

	public final int executeScript(Widget widget, int id) {
		if (widget.scripts == null || id >= widget.scripts.length) {
			return -2;
		}
		try {
			int[] script = widget.scripts[id];
			int value = 0;
			int counter = 0;
			int operator = 0;

			do {
				int instruction = script[counter++];
				int operand = 0;
				byte next = 0;
				if (instruction == 0) {
					return value;
				} else if (instruction == 1) {
					operand = skillLevel[script[counter++]];
				} else if (instruction == 2) {
					operand = levelBase[script[counter++]];
				} else if (instruction == 3) {
					operand = skillExperience[script[counter++]];
				} else if (instruction == 4) {
					Widget other = Widget.widgets[script[counter++]];
					int item = script[counter++];

					if (item >= 0 && item < ItemDefinition.count && (!ItemDefinition.lookup(item).members || membersServer)) {
						for (int slot = 0; slot < other.inventoryIds.length; slot++) {
							if (other.inventoryIds[slot] == item + 1) {
								operand += other.inventoryAmounts[slot];
							}
						}
					}
				} else if (instruction == 5) {
					operand = settings[script[counter++]];
				} else if (instruction == 6) {
					operand = SKILL_EXPERIENCE[levelBase[script[counter++]] - 1];
				} else if (instruction == 7) {
					operand = settings[script[counter++]] * 100 / 46875;
				} else if (instruction == 8) {
					operand = localPlayer.combat;
				} else if (instruction == 9) {
					for (int skill = 0; skill < SkillConstants.SKILL_COUNT; skill++) {
						if (SkillConstants.ENABLED_SKILLS[skill]) {
							operand += levelBase[skill];
						}
					}
				} else if (instruction == 10) {
					Widget other = Widget.widgets[script[counter++]];
					int item = script[counter++] + 1;

					if (item >= 0 && item < ItemDefinition.count && (!ItemDefinition.lookup(item).members || membersServer)) {
						for (int stored : other.inventoryIds) {
							if (stored != item) {
								continue;
							}

							operand = 0x3b9ac9ff;
							break;
						}
					}
				} else if (instruction == 11) {
					operand = runEnergy;
				} else if (instruction == 12) {
					operand = weight;
				} else if (instruction == 13) {
					int bool = settings[script[counter++]];
					int shift = script[counter++];
					operand = (bool & 1 << shift) == 0 ? 0 : 1;
				} else if (instruction == 14) {
					int index = script[counter++];
					VariableBits bits = VariableBits.bits[index];
					int setting = bits.setting;
					int low = bits.low;
					int high = bits.high;
					int mask = BIT_MASKS[high - low];
					operand = settings[setting] >> low & mask;
				} else if (instruction == 15) {
					next = 1;
				} else if (instruction == 16) {
					next = 2;
				} else if (instruction == 17) {
					next = 3;
				} else if (instruction == 18) {
					operand = (localPlayer.worldX >> 7) + regionBaseX;
				} else if (instruction == 19) {
					operand = (localPlayer.worldY >> 7) + regionBaseY;
				} else if (instruction == 20) {
					operand = script[counter++];
				}

				if (next == 0) {
					if (operator == 0) {
						value += operand;
					} else if (operator == 1) {
						value -= operand;
					} else if (operator == 2 && operand != 0) {
						value /= operand;
					} else if (operator == 3) {
						value *= operand;
					}

					operator = 0;
				} else {
					operator = next;
				}
			} while (true);
		} catch (Exception ex) {
			return -1;
		}
	}

	@Override
	public final AppletContext getAppletContext() {
		if (SignLink.applet != null) {
			return SignLink.applet.getAppletContext();
		}
		return super.getAppletContext();
	}

	@Override
	public final URL getCodeBase() {
		if (SignLink.applet != null) {
			return SignLink.applet.getCodeBase();
		}

		try {
			if (super.frame != null) {
				return new URL("http://127.0.0.1:" + (80 + portOffset));
			}
		} catch (Exception _ex) {
		}
		return super.getCodeBase();
	}

	/**
	 * Gets a string representing the specified integer, returning "*" if the value is less than 999,999,999.
	 * 
	 * @param value The value.
	 * @return The string.
	 */
	public final String getDisplayableAmountString(int value) {
		if (value < 999999999) {
			return String.valueOf(value);
		}
		return "*";
	}

	@Override
	public final Component getFrame() {
		if (SignLink.applet != null) {
			return SignLink.applet;
		} else if (super.frame != null) {
			return super.frame;
		}

		return this;
	}

	public final String getHost() {
		if (SignLink.applet != null) {
			return SignLink.applet.getDocumentBase().getHost().toLowerCase();
		} else if (super.frame != null) {
			return "runescape.com";
		}

		return super.getDocumentBase().getHost().toLowerCase();
	}

	@Override
	public final String getParameter(String parameter) {
		if (SignLink.applet != null) {
			return SignLink.applet.getParameter(parameter);
		}

		return super.getParameter(parameter);
	}

	public final void addIgnore(long name) {
		if (name == 0L) {
			return;
		}
		if (ignoredCount >= 100) {
			addChatMessage(0, "Your ignore list is full. Max of 100 hit", "");
			return;
		}
		String s = StringUtils.format(StringUtils.decodeBase37(name));
		for (int index = 0; index < ignoredCount; index++) {
			if (ignoredPlayers[index] == name) {
				addChatMessage(0, s + " is already on your ignore list", "");
				return;
			}
		}

		for (int k = 0; k < friendCount; k++) {
			if (friends[k] == name) {
				addChatMessage(0, "Please remove " + s + " from your friend list first", "");
				return;
			}
		}

		ignoredPlayers[ignoredCount++] = name;
		redrawTabArea = true;
		outgoing.writeOpcode(133);
		outgoing.writeLong(name);
	}

	@Override
	public final void init() {
		nodeId = Integer.parseInt(getParameter("nodeid"));
		portOffset = Integer.parseInt(getParameter("portoff"));
		String lowMemory = getParameter("lowmem");
		if (lowMemory != null && lowMemory.equals("1")) {
			setLowMemory();
		} else {
			setHighMemory();
		}
		String free = getParameter("free");

		if (free != null && free.equals("1")) {
			membersServer = false;
		} else {
			membersServer = true;
		}
		startApplet(503, 765);
	}

	public final boolean displayMessageFrom(String s) {
		if (s == null) {
			return false;
		}
		for (int index = 0; index < friendCount; index++) {
			if (s.equalsIgnoreCase(friendUsernames[index])) {
				return true;
			}
		}

		return s.equalsIgnoreCase(localPlayer.name);
	}

	@Override
	public final void load() {
		drawLoadingText(20, "Starting up");
		if (SignLink.sunJava) {
			super.minimumSleepTime = 5;
		}
		if (clientLoaded) {
			gameAlreadyLoaded = true;
			return;
		}
		clientLoaded = true;
		boolean validHost = false;
		String host = getHost();

		if (host.endsWith("jagex.com")) {
			validHost = true;
		}
		if (host.endsWith("runescape.com")) {
			validHost = true;
		}
		if (host.endsWith("192.168.1.2")) {
			validHost = true;
		}
		if (host.endsWith("192.168.1.229")) {
			validHost = true;
		}
		if (host.endsWith("192.168.1.228")) {
			validHost = true;
		}
		if (host.endsWith("192.168.1.227")) {
			validHost = true;
		}
		if (host.endsWith("192.168.1.226")) {
			validHost = true;
		}
		if (host.endsWith("127.0.0.1")) {
			validHost = true;
		}

		if (!validHost) {
			unableToLoad = true;
			return;
		}

		if (SignLink.cache != null) {
			for (int index = 0; index < 5; index++) {
				indices[index] = new Index(SignLink.indices[index], SignLink.cache, index + 1, 0x7a120);
			}

		}
		try {
			requestCrcs();
			titleScreen = createArchive(1, "title screen", "title", archiveCRCs[1], 25);
			smallFont = new Font(false, "p11_full", titleScreen);
			frameFont = new Font(false, "p12_full", titleScreen);
			boldFont = new Font(false, "b12_full", titleScreen);
			aClass30_Sub2_Sub1_Sub4_1273 = new Font(true, "q8_full", titleScreen);
			method56();
			method51();

			Archive configArchive = createArchive(2, "config", "config", archiveCRCs[2], 30);
			Archive interfaceArchive = createArchive(3, "interface", "interface", archiveCRCs[3], 35);
			Archive graphicsArchive = createArchive(4, "2d graphics", "media", archiveCRCs[4], 40);
			Archive textureArchive = createArchive(6, "textures", "textures", archiveCRCs[6], 45);
			Archive chatArchive = createArchive(7, "chat system", "wordenc", archiveCRCs[7], 50);
			Archive soundArchive = createArchive(8, "sound effects", "sounds", archiveCRCs[8], 55);

			aByteArrayArrayArray1258 = new byte[4][104][104];
			anIntArrayArrayArray1214 = new int[4][105][105];
			scene = new Scene(104, 104, 4, anIntArrayArrayArray1214);
			for (int j = 0; j < 4; j++) {
				collisionMaps[j] = new CollisionMap(104, 104);
			}

			aClass30_Sub2_Sub1_Sub1_1263 = new DirectSprite(512, 512);
			Archive versionArchive = createArchive(5, "update list", "versionlist", archiveCRCs[5], 60);
			drawLoadingText(60, "Connecting to update server");
			provider = new ResourceProvider();
			provider.init(versionArchive, this);
			Frame.init(provider.frameCount());
			Model.init(provider.getCount(0), provider);

			if (!lowMemory) {
				musicId = 0;
				try {
					musicId = Integer.parseInt(getParameter("music"));
				} catch (Exception _ex) {
				}
				fadeMusic = true;
				provider.provide(2, musicId);
				while (provider.remaining() > 0) {
					processLoadedResources();
					try {
						Thread.sleep(100L);
					} catch (Exception _ex) {
					}
					if (provider.errors > 3) {
						error("ondemand");
						return;
					}
				}
			}

			drawLoadingText(65, "Requesting animations");
			int remaining = provider.getCount(1);
			for (int file = 0; file < remaining; file++) {
				provider.provide(1, file);
			}

			while (provider.remaining() > 0) {
				int complete = remaining - provider.remaining();
				if (complete > 0) {
					drawLoadingText(65, "Loading animations - " + complete * 100 / remaining + "%");
				}
				processLoadedResources();
				try {
					Thread.sleep(100L);
				} catch (Exception _ex) {
				}
				if (provider.errors > 3) {
					error("ondemand");
					return;
				}
			}

			drawLoadingText(70, "Requesting models");
			remaining = provider.getCount(0);
			for (int file = 0; file < remaining; file++) {
				int attributes = provider.getModelAttributes(file);
				if ((attributes & 1) != 0) {
					provider.provide(0, file);
				}
			}

			remaining = provider.remaining();
			while (provider.remaining() > 0) {
				int complete = remaining - provider.remaining();
				if (complete > 0) {
					drawLoadingText(70, "Loading models - " + complete * 100 / remaining + "%");
				}
				processLoadedResources();
				try {
					Thread.sleep(100L);
				} catch (Exception _ex) {
				}
			}

			if (indices[0] != null) {
				drawLoadingText(75, "Requesting maps");
				provider.provide(3, provider.resolve(47, 48, 0));
				provider.provide(3, provider.resolve(47, 48, 1));
				provider.provide(3, provider.resolve(48, 48, 0));
				provider.provide(3, provider.resolve(48, 48, 1));
				provider.provide(3, provider.resolve(49, 48, 0));
				provider.provide(3, provider.resolve(49, 48, 1));
				provider.provide(3, provider.resolve(47, 47, 0));
				provider.provide(3, provider.resolve(47, 47, 1));
				provider.provide(3, provider.resolve(48, 47, 0));
				provider.provide(3, provider.resolve(48, 47, 1));
				provider.provide(3, provider.resolve(48, 148, 0));
				provider.provide(3, provider.resolve(48, 148, 1));
				remaining = provider.remaining();

				while (provider.remaining() > 0) {
					int complete = remaining - provider.remaining();
					if (complete > 0) {
						drawLoadingText(75, "Loading maps - " + complete * 100 / remaining + "%");
					}
					processLoadedResources();

					try {
						Thread.sleep(100L);
					} catch (Exception _ex) {
					}
				}
			}

			remaining = provider.getCount(0);
			for (int file = 0; file < remaining; file++) {
				int attributes = provider.getModelAttributes(file);
				byte priority = 0;
				if ((attributes & 8) != 0) {
					priority = 10;
				} else if ((attributes & 0x20) != 0) {
					priority = 9;
				} else if ((attributes & 0x10) != 0) {
					priority = 8;
				} else if ((attributes & 0x40) != 0) {
					priority = 7;
				} else if ((attributes & 0x80) != 0) {
					priority = 6;
				} else if ((attributes & 2) != 0) {
					priority = 5;
				} else if ((attributes & 4) != 0) {
					priority = 4;
				}
				if ((attributes & 1) != 0) {
					priority = 3;
				}
				if (priority != 0) {
					provider.requestExtra(0, file, priority);
				}
			}

			provider.preloadMaps(membersServer);
			if (!lowMemory) {
				int musicCount = provider.getCount(2);
				for (int music = 1; music < musicCount; music++) {
					if (provider.highPriorityMusic(music)) {
						provider.requestExtra(2, music, (byte) 1);
					}
				}
			}

			drawLoadingText(80, "Unpacking media");
			inventoryBackground = new IndexedImage(graphicsArchive, "invback", 0);
			chatBackground = new IndexedImage(graphicsArchive, "chatback", 0);
			mapBackground = new IndexedImage(graphicsArchive, "mapback", 0);
			backBase1 = new IndexedImage(graphicsArchive, "backbase1", 0);
			backBase2 = new IndexedImage(graphicsArchive, "backbase2", 0);
			backHmid1 = new IndexedImage(graphicsArchive, "backhmid1", 0);
			for (int icon = 0; icon < 13; icon++) {
				sideIcons[icon] = new IndexedImage(graphicsArchive, "sideicons", icon);
			}

			compass = new DirectSprite(graphicsArchive, "compass", 0);
			mapEdge = new DirectSprite(graphicsArchive, "mapedge", 0);
			mapEdge.resize();

			try {
				for (int scene = 0; scene < 100; scene++) {
					mapScenes[scene] = new IndexedImage(graphicsArchive, "mapscene", scene);
				}
			} catch (Exception ex) {
			}

			try {
				for (int function = 0; function < 100; function++) {
					mapFunctions[function] = new DirectSprite(graphicsArchive, "mapfunction", function);
				}
			} catch (Exception ex) {
			}

			try {
				for (int mark = 0; mark < 20; mark++) {
					hitMarks[mark] = new DirectSprite(graphicsArchive, "hitmarks", mark);
				}
			} catch (Exception ex) {
			}

			try {
				for (int icon = 0; icon < 20; icon++) {
					headIcons[icon] = new DirectSprite(graphicsArchive, "headicons", icon);
				}
			} catch (Exception ex) {
			}

			firstMapmarker = new DirectSprite(graphicsArchive, "mapmarker", 0);
			secondMapmarker = new DirectSprite(graphicsArchive, "mapmarker", 1);
			for (int i = 0; i < 8; i++) {
				crosses[i] = new DirectSprite(graphicsArchive, "cross", i);
			}

			itemMapdot = new DirectSprite(graphicsArchive, "mapdots", 0);
			npcMapdot = new DirectSprite(graphicsArchive, "mapdots", 1);
			playerMapdot = new DirectSprite(graphicsArchive, "mapdots", 2);
			friendMapdot = new DirectSprite(graphicsArchive, "mapdots", 3);
			teamMapdot = new DirectSprite(graphicsArchive, "mapdots", 4);
			aClass30_Sub2_Sub1_Sub2_1024 = new IndexedImage(graphicsArchive, "scrollbar", 0);
			aClass30_Sub2_Sub1_Sub2_1025 = new IndexedImage(graphicsArchive, "scrollbar", 1);
			aClass30_Sub2_Sub1_Sub2_1143 = new IndexedImage(graphicsArchive, "redstone1", 0);
			aClass30_Sub2_Sub1_Sub2_1144 = new IndexedImage(graphicsArchive, "redstone2", 0);
			aClass30_Sub2_Sub1_Sub2_1145 = new IndexedImage(graphicsArchive, "redstone3", 0);
			aClass30_Sub2_Sub1_Sub2_1146 = new IndexedImage(graphicsArchive, "redstone1", 0);
			aClass30_Sub2_Sub1_Sub2_1146.flipHorizontally();
			aClass30_Sub2_Sub1_Sub2_1147 = new IndexedImage(graphicsArchive, "redstone2", 0);
			aClass30_Sub2_Sub1_Sub2_1147.flipHorizontally();
			aClass30_Sub2_Sub1_Sub2_865 = new IndexedImage(graphicsArchive, "redstone1", 0);
			aClass30_Sub2_Sub1_Sub2_865.flipVertically();
			aClass30_Sub2_Sub1_Sub2_866 = new IndexedImage(graphicsArchive, "redstone2", 0);
			aClass30_Sub2_Sub1_Sub2_866.flipVertically();
			aClass30_Sub2_Sub1_Sub2_867 = new IndexedImage(graphicsArchive, "redstone3", 0);
			aClass30_Sub2_Sub1_Sub2_867.flipVertically();
			aClass30_Sub2_Sub1_Sub2_868 = new IndexedImage(graphicsArchive, "redstone1", 0);
			aClass30_Sub2_Sub1_Sub2_868.flipHorizontally();
			aClass30_Sub2_Sub1_Sub2_868.flipVertically();
			aClass30_Sub2_Sub1_Sub2_869 = new IndexedImage(graphicsArchive, "redstone2", 0);
			aClass30_Sub2_Sub1_Sub2_869.flipHorizontally();
			aClass30_Sub2_Sub1_Sub2_869.flipVertically();

			for (int icon = 0; icon < 2; icon++) {
				modIcons[icon] = new IndexedImage(graphicsArchive, "mod_icons", icon);
			}

			DirectSprite sprite = new DirectSprite(graphicsArchive, "backleft1", 0);
			backLeft1Buffer = new ProducingGraphicsBuffer(getFrame(), sprite.width, sprite.height);
			sprite.method346(0, 0);
			sprite = new DirectSprite(graphicsArchive, "backleft2", 0);
			backLeft2Buffer = new ProducingGraphicsBuffer(getFrame(), sprite.width, sprite.height);
			sprite.method346(0, 0);
			sprite = new DirectSprite(graphicsArchive, "backright1", 0);
			backRight1Buffer = new ProducingGraphicsBuffer(getFrame(), sprite.width, sprite.height);
			sprite.method346(0, 0);
			sprite = new DirectSprite(graphicsArchive, "backright2", 0);
			backRight2Buffer = new ProducingGraphicsBuffer(getFrame(), sprite.width, sprite.height);
			sprite.method346(0, 0);
			sprite = new DirectSprite(graphicsArchive, "backtop1", 0);
			backTopBuffer = new ProducingGraphicsBuffer(getFrame(), sprite.width, sprite.height);
			sprite.method346(0, 0);
			sprite = new DirectSprite(graphicsArchive, "backvmid1", 0);
			aClass15_908 = new ProducingGraphicsBuffer(getFrame(), sprite.width, sprite.height);
			sprite.method346(0, 0);
			sprite = new DirectSprite(graphicsArchive, "backvmid2", 0);
			aClass15_909 = new ProducingGraphicsBuffer(getFrame(), sprite.width, sprite.height);
			sprite.method346(0, 0);
			sprite = new DirectSprite(graphicsArchive, "backvmid3", 0);
			aClass15_910 = new ProducingGraphicsBuffer(getFrame(), sprite.width, sprite.height);
			sprite.method346(0, 0);
			sprite = new DirectSprite(graphicsArchive, "backhmid2", 0);
			aClass15_911 = new ProducingGraphicsBuffer(getFrame(), sprite.width, sprite.height);
			sprite.method346(0, 0);

			int red = (int) (Math.random() * 21D) - 10;
			int green = (int) (Math.random() * 21D) - 10;
			int blue = (int) (Math.random() * 21D) - 10;
			int offset = (int) (Math.random() * 41D) - 20;
			for (int i = 0; i < 100; i++) {
				if (mapFunctions[i] != null) {
					mapFunctions[i].recolour(red + offset, green + offset, blue + offset);
				}
				if (mapScenes[i] != null) {
					mapScenes[i].offsetColour(red + offset, green + offset, blue + offset);
				}
			}

			drawLoadingText(83, "Unpacking textures");
			Rasterizer.loadFloorImages(textureArchive);
			Rasterizer.generatePalette(0.8D);
			Rasterizer.method367(20);
			drawLoadingText(86, "Unpacking config");
			Animation.init(configArchive);
			ObjectDefinition.init(configArchive);
			Floor.init(configArchive);
			ItemDefinition.init(configArchive);
			NpcDefinition.init(configArchive);
			IdentityKit.init(configArchive);
			Graphic.init(configArchive);
			VariableParameter.init(configArchive);
			VariableBits.init(configArchive);
			ItemDefinition.membersServer = membersServer;

			if (!lowMemory) {
				drawLoadingText(90, "Unpacking sounds");
				byte[] data = soundArchive.extract("sounds.dat");
				Track.load(new Buffer(data));
			}
			drawLoadingText(95, "Unpacking interfaces");
			Font[] fonts = { smallFont, frameFont, boldFont, aClass30_Sub2_Sub1_Sub4_1273 };
			Widget.load(interfaceArchive, graphicsArchive, fonts);

			drawLoadingText(100, "Preparing game engine");
			for (int y = 0; y < 33; y++) {
				int firstX = 999;
				int lastX = 0;
				for (int x = 0; x < 34; x++) {
					if (mapBackground.raster[x + y * mapBackground.width] == 0) {
						if (firstX == 999) {
							firstX = x;
						}
						continue;
					}
					if (firstX == 999) {
						continue;
					}
					lastX = x;
					break;
				}

				anIntArray968[y] = firstX;
				anIntArray1057[y] = lastX - firstX;
			}

			for (int y = 5; y < 156; y++) {
				int firstX = 999;
				int currentX = 0;
				for (int x = 25; x < 172; x++) {
					if (mapBackground.raster[x + y * mapBackground.width] == 0 && (x > 34 || y > 34)) {
						if (firstX == 999) {
							firstX = x;
						}
						continue;
					}
					if (firstX == 999) {
						continue;
					}
					currentX = x;
					break;
				}

				anIntArray1052[y - 5] = firstX - 25;
				anIntArray1229[y - 5] = currentX - firstX;
			}

			Rasterizer.method365(479, 96);
			anIntArray1180 = Rasterizer.scanOffsets;
			Rasterizer.method365(190, 261);
			anIntArray1181 = Rasterizer.scanOffsets;
			Rasterizer.method365(512, 334);
			anIntArray1182 = Rasterizer.scanOffsets;
			int ai[] = new int[9];
			for (int i8 = 0; i8 < 9; i8++) {
				int theta = 128 + i8 * 32 + 15;
				int l8 = 600 + theta * 3;
				int i9 = Rasterizer.SINE[theta];
				ai[i8] = l8 * i9 >> 16;
			}

			Scene.method310(500, 800, 512, 334, ai);
			MessageCensor.init(chatArchive);
			mouseCapturer = new MouseCapturer(this);
			startRunnable(mouseCapturer, 10);
			GameObject.client = this;
			ObjectDefinition.client = this;
			NpcDefinition.client = this;
			return;
		} catch (Exception exception) {
			SignLink.reportError("loaderror " + loadingScreenText + " " + anInt1079);
		}
		error = true;
	}

	public final void login(String name, String password, boolean reconnecting) {
		SignLink.error = name;
		try {
			if (!reconnecting) {
				firstLoginMessage = "";
				secondLoginMessage = "Connecting to server...";
				drawLoginScreen(true);
			}

			primary = new BufferedConnection(this, openSocket(43594 + portOffset));
			long encoded = StringUtils.encodeBase37(name);
			int nameHash = (int) (encoded >> 16 & 31L);
			outgoing.position = 0;
			outgoing.writeByte(14);
			outgoing.writeByte(nameHash);
			primary.write(outgoing.payload, 2, 0);
			for (int i = 0; i < 8; i++) {
				primary.read();
			}

			int response = primary.read();
			int copy = response;
			if (response == 0) {
				primary.read(incomingBuffer.payload, 0, 8);
				incomingBuffer.position = 0;
				serverSeed = incomingBuffer.readLong();
				int[] seed = new int[4];
				seed[0] = (int) (Math.random() * 99999999D);
				seed[1] = (int) (Math.random() * 99999999D);
				seed[2] = (int) (serverSeed >> 32);
				seed[3] = (int) serverSeed;
				outgoing.position = 0;
				outgoing.writeByte(10); // Secure id
				outgoing.writeInt(seed[0]);
				outgoing.writeInt(seed[1]);
				outgoing.writeInt(seed[2]);
				outgoing.writeInt(seed[3]);
				outgoing.writeInt(SignLink.uid);
				outgoing.writeJString(name);
				outgoing.writeJString(password);
				outgoing.encodeRSA(RSA_EXPONENT, RSA_MODULUS);
				loginBuffer.position = 0;
				if (reconnecting) {
					loginBuffer.writeByte(18);
				} else {
					loginBuffer.writeByte(16);
				}
				loginBuffer.writeByte(outgoing.position + 36 + 1 + 1 + 2);
				loginBuffer.writeByte(255); // magic number
				loginBuffer.writeShort(317); // revision
				loginBuffer.writeByte(lowMemory ? 1 : 0);
				for (int index = 0; index < 9; index++) {
					loginBuffer.writeInt(archiveCRCs[index]);
				}

				loginBuffer.writeBytes(outgoing.payload, 0, outgoing.position);
				outgoing.encryption = new IsaacCipher(seed);
				for (int index = 0; index < 4; index++) {
					seed[index] += 50;
				}

				encryption = new IsaacCipher(seed);
				primary.write(loginBuffer.payload, loginBuffer.position, 0);
				response = primary.read();
			}

			if (response == 1) {
				try {
					Thread.sleep(2000L);
				} catch (Exception _ex) {
				}

				login(name, password, reconnecting);
			} else if (response == 2) {
				playerPrivelage = primary.read();
				flaggedAccount = primary.read() == 1;
				aLong1220 = 0L;
				duplicateClickCount = 0;
				mouseCapturer.capturedCoordinateCount = 0;
				super.hasFocus = true;
				wasFocused = true;
				loggedIn = true;
				frame.setResizable(true);
				outgoing.position = 0;
				incomingBuffer.position = 0;
				opcode = -1;
				lastOpcode = -1;
				secondLastOpcode = -1;
				thirdLastOpcode = -1;
				packetSize = 0;
				timeoutCounter = 0;
				systemUpdateTime = 0;
				anInt1011 = 0;
				headIconDrawType = 0;
				menuActionRow = 0;
				menuOpen = false;
				super.timeIdle = 0;
				for (int index = 0; index < 100; index++) {
					chatMessages[index] = null;
				}

				selectedItemId = 0;
				selectedSpellId = 0;
				loadingStage = 0;
				trackCount = 0;
				anInt1278 = (int) (Math.random() * 100D) - 50;
				anInt1131 = (int) (Math.random() * 110D) - 55;
				anInt896 = (int) (Math.random() * 80D) - 40;
				anInt1209 = (int) (Math.random() * 120D) - 60;
				anInt1170 = (int) (Math.random() * 30D) - 20;
				cameraYaw = (int) (Math.random() * 20D) - 10 & 0x7ff;
				minimapState = 0;
				anInt985 = -1;
				destinationX = 0;
				destinationY = 0;
				playerCount = 0;
				npcCount = 0;
				for (int index = 0; index < maximumPlayers; index++) {
					players[index] = null;
					playerSynchronizationBuffers[index] = null;
				}

				for (int index = 0; index < 16384; index++) {
					npcs[index] = null;
				}

				localPlayer = players[internalLocalPlayerIndex] = new Player();
				projectiles.clear();
				incompleteAnimables.clear();
				for (int z = 0; z < 4; z++) {
					for (int x = 0; x < 104; x++) {
						for (int y = 0; y < 104; y++) {
							groundItems[z][x][y] = null;
						}
					}
				}

				spawns = new Deque();
				friendServerStatus = 0;
				friendCount = 0;
				dialogueId = -1;
				backDialogueId = -1;
				openInterfaceId = -1;
				inventoryOverlayInterfaceId = -1;
				currentStatusInterface = -1;
				aBoolean1149 = false;
				tabId = 3;
				inputDialogueState = 0;
				menuOpen = false;
				messagePromptRaised = false;
				clickToContinueString = null;
				multicombat = 0;
				flashingSidebarId = -1;
				maleAvatar = true;
				changeCharacterGender();
				for (int index = 0; index < 5; index++) {
					characterDesignColours[index] = 0;
				}

				for (int index = 0; index < 5; index++) {
					aStringArray1127[index] = null;
					aBooleanArray1128[index] = false;
				}

				anInt1175 = 0;
				anInt1134 = 0;
				anInt986 = 0;
				anInt1288 = 0;
				anInt924 = 0;
				anInt1188 = 0;
				anInt1155 = 0;
				anInt1226 = 0;
				drawScreen();
			} else if (response == 3) {
				firstLoginMessage = "";
				secondLoginMessage = "Invalid username or password.";
			} else if (response == 4) {
				firstLoginMessage = "Your account has been disabled.";
				secondLoginMessage = "Please check your message-centre for details.";
			} else if (response == 5) {
				firstLoginMessage = "Your account is already logged in.";
				secondLoginMessage = "Try again in 60 secs...";
			} else if (response == 6) {
				firstLoginMessage = "RuneScape has been updated!";
				secondLoginMessage = "Please reload this page.";
			} else if (response == 7) {
				firstLoginMessage = "This world is full.";
				secondLoginMessage = "Please use a different world.";
			} else if (response == 8) {
				firstLoginMessage = "Unable to connect.";
				secondLoginMessage = "Login server offline.";
			} else if (response == 9) {
				firstLoginMessage = "Login limit exceeded.";
				secondLoginMessage = "Too many connections from your address.";
			} else if (response == 10) {
				firstLoginMessage = "Unable to connect.";
				secondLoginMessage = "Bad session id.";
			} else if (response == 11) {
				secondLoginMessage = "Login server rejected session.";
				secondLoginMessage = "Please try again.";
			} else if (response == 12) {
				firstLoginMessage = "You need a members account to login to this world.";
				secondLoginMessage = "Please subscribe, or use a different world.";
			} else if (response == 13) {
				firstLoginMessage = "Could not complete login.";
				secondLoginMessage = "Please try using a different world.";
			} else if (response == 14) {
				firstLoginMessage = "The server is being updated.";
				secondLoginMessage = "Please wait 1 minute and try again.";
			} else if (response == 15) {
				loggedIn = true;
                frame.setResizable(true);
				outgoing.position = 0;
				incomingBuffer.position = 0;
				opcode = -1;
				lastOpcode = -1;
				secondLastOpcode = -1;
				thirdLastOpcode = -1;
				packetSize = 0;
				timeoutCounter = 0;
				systemUpdateTime = 0;
				menuActionRow = 0;
				menuOpen = false;
				loadingStartTime = System.currentTimeMillis();
			} else if (response == 16) {
				firstLoginMessage = "Login attempts exceeded.";
				secondLoginMessage = "Please wait 1 minute and try again.";
			} else if (response == 17) {
				firstLoginMessage = "You are standing in a members-only area.";
				secondLoginMessage = "To play on this world move to a free area first";
			} else if (response == 20) {
				firstLoginMessage = "Invalid loginserver requested";
				secondLoginMessage = "Please try using a different world.";
			} else if (response == 21) {
				for (int time = primary.read(); time >= 0; time--) {
					firstLoginMessage = "You have only just left another world";
					secondLoginMessage = "Your profile will be transferred in: " + time + " seconds";
					drawLoginScreen(true);
					try {
						Thread.sleep(1000L);
					} catch (Exception _ex) {
					}
				}

				login(name, password, reconnecting);
			} else if (response == -1) {
				if (copy == 0) {
					if (loginFailures < 2) {
						try {
							Thread.sleep(2000L);
						} catch (Exception _ex) {
						}
						loginFailures++;
						login(name, password, reconnecting);
						return;
					}

					firstLoginMessage = "No response from loginserver";
					secondLoginMessage = "Please wait 1 minute and try again.";
					return;
				}

				firstLoginMessage = "No response from server";
				secondLoginMessage = "Please try using a different world.";
				return;
			}

			System.out.println("response:" + response);
			firstLoginMessage = "Unexpected server response";
			secondLoginMessage = "Please try using a different world.";
			return;
		} catch (IOException ex) {
			firstLoginMessage = "";
		}

		secondLoginMessage = "Error connecting to server.";
	}

	@Override
	public final void method10() {
		redrawBorders = true;
	}

	public final void method100(Mob mob) {
		if (mob.rotation == 0) {
			return;
		}

		if (mob.interactingMob != -1 && mob.interactingMob < 32768) {
			Npc npc = npcs[mob.interactingMob];
			if (npc != null) {
				int x = mob.worldX - npc.worldX;
				int y = mob.worldY - npc.worldY;
				if (x != 0 || y != 0) {
					mob.nextStepOrientation = (int) (Math.atan2(x, y) * 325.949D) & 0x7ff;
				}
			}
		}

		if (mob.interactingMob >= 32768) {
			int index = mob.interactingMob - 32768;
			if (index == localPlayerIndex) {
				index = internalLocalPlayerIndex;
			}
			Player player = players[index];
			if (player != null) {
				int x = mob.worldX - player.worldX;
				int y = mob.worldY - player.worldY;
				if (x != 0 || y != 0) {
					mob.nextStepOrientation = (int) (Math.atan2(x, y) * 325.949D) & 0x7ff;
				}
			}
		}

		if ((mob.faceX != 0 || mob.faceY != 0) && (mob.remainingPath == 0 || mob.anInt1503 > 0)) {
			int x = mob.worldX - (mob.faceX - regionBaseX - regionBaseX) * 64;
			int y = mob.worldY - (mob.faceY - regionBaseY - regionBaseY) * 64;
			if (x != 0 || y != 0) {
				mob.nextStepOrientation = (int) (Math.atan2(x, y) * 325.949D) & 0x7ff;
			}
			mob.faceX = 0;
			mob.faceY = 0;
		}

		int yawDelta = mob.nextStepOrientation - mob.orientation & 0x7ff;
		if (yawDelta != 0) {
			if (yawDelta < mob.rotation || yawDelta > 0x800 - mob.rotation) {
				mob.orientation = mob.nextStepOrientation;
			} else if (yawDelta > 0x400) {
				mob.orientation -= mob.rotation;
			} else {
				mob.orientation += mob.rotation;
			}
			mob.orientation &= 0x7ff;

			if (mob.movementAnimation == mob.idleAnimation && mob.orientation != mob.nextStepOrientation) {
				if (mob.turnAnimation != -1) {
					mob.movementAnimation = mob.turnAnimation;
					return;
				}
				mob.movementAnimation = mob.walkingAnimation;
			}
		}
	}

	public final void method101(Mob character) {
		character.animationStretches = false;
		if (character.movementAnimation != -1) {
			Animation animation = Animation.animations[character.movementAnimation];
			character.anInt1519++;
			if (character.displayedMovementFrames < animation.frameCount
					&& character.anInt1519 > animation.duration(character.displayedMovementFrames)) {
				character.anInt1519 = 0;
				character.displayedMovementFrames++;
			}
			if (character.displayedMovementFrames >= animation.frameCount) {
				character.anInt1519 = 0;
				character.displayedMovementFrames = 0;
			}
		}
		if (character.graphicId != -1 && tick >= character.graphicDelay) {
			if (character.currentAnimation < 0) {
				character.currentAnimation = 0;
			}
			Animation graphicAnimation = Graphic.graphics[character.graphicId].animation;
			for (character.anInt1522++; character.currentAnimation < graphicAnimation.frameCount
					&& character.anInt1522 > graphicAnimation.duration(character.currentAnimation); character.currentAnimation++) {
				character.anInt1522 -= graphicAnimation.duration(character.currentAnimation);
			}

			if (character.currentAnimation >= graphicAnimation.frameCount
					&& (character.currentAnimation < 0 || character.currentAnimation >= graphicAnimation.frameCount)) {
				character.graphicId = -1;
			}
		}
		if (character.emoteAnimation != -1 && character.animationDelay <= 1) {
			Animation emoteAnimation = Animation.animations[character.emoteAnimation];
			if (emoteAnimation.animatingPrecedence == 1 && character.anInt1542 > 0 && character.startForceMovement <= tick
					&& character.endForceMovement < tick) {
				character.animationDelay = 1;
				return;
			}
		}
		if (character.emoteAnimation != -1 && character.animationDelay == 0) {
			Animation emoteAnimation = Animation.animations[character.emoteAnimation];
			for (character.anInt1528++; character.displayedEmoteFrames < emoteAnimation.frameCount
					&& character.anInt1528 > emoteAnimation.duration(character.displayedEmoteFrames); character.displayedEmoteFrames++) {
				character.anInt1528 -= emoteAnimation.duration(character.displayedEmoteFrames);
			}

			if (character.displayedEmoteFrames >= emoteAnimation.frameCount) {
				character.displayedEmoteFrames -= emoteAnimation.loopOffset;
				character.currentAnimationLoops++;
				if (character.currentAnimationLoops >= emoteAnimation.maximumLoops) {
					character.emoteAnimation = -1;
				}
				if (character.displayedEmoteFrames < 0 || character.displayedEmoteFrames >= emoteAnimation.frameCount) {
					character.emoteAnimation = -1;
				}
			}
			character.animationStretches = emoteAnimation.stretches;
		}
		if (character.animationDelay > 0) {
			character.animationDelay--;
		}
	}

	public final boolean method103(Widget widget) {
		int row = widget.anInt214;
		if (row >= 1 && row <= 200 || row >= 701 && row <= 900) {
			if (row >= 801) {
				row -= 701;
			} else if (row >= 701) {
				row -= 601;
			} else if (row >= 101) {
				row -= 101;
			} else {
				row--;
			}
			menuActionTexts[menuActionRow] = "Remove @whi@" + friendUsernames[row];
			menuActionIds[menuActionRow] = 792;
			menuActionRow++;
			menuActionTexts[menuActionRow] = "Message @whi@" + friendUsernames[row];
			menuActionIds[menuActionRow] = 639;
			menuActionRow++;
			return true;
		}
		if (row >= 401 && row <= 500) {
			menuActionTexts[menuActionRow] = "Remove @whi@" + widget.hiddenText;
			menuActionIds[menuActionRow] = 322;
			menuActionRow++;
			return true;
		}
		return false;
	}

	public final void method105(int j, int x, Widget widget, int y) {
		if (widget.type != 0 || widget.children == null) {
			return;
		}
		if (widget.aBoolean266 && anInt1026 != widget.id && anInt1048 != widget.id && anInt1039 != widget.id) {
			return;
		}
		int clipLeft = Raster.clipLeft;
		int clipBottom = Raster.clipBottom;
		int clipRight = Raster.clipRight;
		int clipTop = Raster.clipTop;
		Raster.setBounds(y + widget.height, x, x + widget.width, y);
		int childCount = widget.children.length;
		for (int childIndex = 0; childIndex < childCount; childIndex++) {
			int currentX = widget.childX[childIndex] + x;
			int currentY = widget.childY[childIndex] + y - j;
			Widget child = Widget.widgets[widget.children[childIndex]];
			currentX += child.horizontalDrawOffset;
			currentY += child.verticalDrawOffset;
			if (child.anInt214 > 0) {
				method75(child);
			}
			if (child.type == 0) {
				if (child.scrollPosition > child.scrollLimit - child.height) {
					child.scrollPosition = child.scrollLimit - child.height;
				}
				if (child.scrollPosition < 0) {
					child.scrollPosition = 0;
				}
				method105(child.scrollPosition, currentX, child, currentY);
				if (child.scrollLimit > child.height) {
					method30(child.height, child.scrollPosition, currentY, currentX + child.width, child.scrollLimit);
				}
			} else if (child.type != 1) {
				if (child.type == 2) {
					int item = 0;
					for (int childY = 0; childY < child.height; childY++) {
						for (int childX = 0; childX < child.width; childX++) {
							int componentX = currentX + childX * (32 + child.spritePaddingX);
							int componentY = currentY + childY * (32 + child.spritePaddingY);
							if (item < 20) {
								componentX += child.spriteX[item];
								componentY += child.spriteY[item];
							}
							if (child.inventoryIds[item] > 0) {
								int k6 = 0;
								int j7 = 0;
								int itemId = child.inventoryIds[item] - 1;
								if (componentX > Raster.clipLeft - 32 && componentX < Raster.clipRight
										&& componentY > Raster.clipBottom - 32 && componentY < Raster.clipTop || anInt1086 != 0
										&& anInt1085 == item) {
									int colour = 0;
									if (selectedItemId == 1 && anInt1283 == item && anInt1284 == child.id) {
										colour = 0xffffff;
									}
									DirectSprite sprite = ItemDefinition.sprite(itemId, child.inventoryAmounts[item], colour);
									if (sprite != null) {
										if (anInt1086 != 0 && anInt1085 == item && anInt1084 == child.id) {
											k6 = super.mouseEventX - anInt1087;
											j7 = super.mouseEventY - anInt1088;
											if (k6 < 5 && k6 > -5) {
												k6 = 0;
											}
											if (j7 < 5 && j7 > -5) {
												j7 = 0;
											}
											if (anInt989 < 5) {
												k6 = 0;
												j7 = 0;
											}
											sprite.method350(componentX + k6, componentY + j7, 128);
											if (componentY + j7 < Raster.clipBottom && widget.scrollPosition > 0) {
												int i10 = tickDelta * (Raster.clipBottom - componentY - j7) / 3;
												if (i10 > tickDelta * 10) {
													i10 = tickDelta * 10;
												}
												if (i10 > widget.scrollPosition) {
													i10 = widget.scrollPosition;
												}
												widget.scrollPosition -= i10;
												anInt1088 += i10;
											}
											if (componentY + j7 + 32 > Raster.clipTop
													&& widget.scrollPosition < widget.scrollLimit - widget.height) {
												int j10 = tickDelta * (componentY + j7 + 32 - Raster.clipTop) / 3;
												if (j10 > tickDelta * 10) {
													j10 = tickDelta * 10;
												}
												if (j10 > widget.scrollLimit - widget.height - widget.scrollPosition) {
													j10 = widget.scrollLimit - widget.height - widget.scrollPosition;
												}
												widget.scrollPosition += j10;
												anInt1088 -= j10;
											}
										} else if (anInt1246 != 0 && anInt1245 == item && anInt1244 == child.id) {
											sprite.method350(componentX, componentY, 128);
										} else {
											sprite.drawSprite(componentX, componentY);
										}
										if (sprite.resizeWidth == 33 || child.inventoryAmounts[item] != 1) {
											int amount = child.inventoryAmounts[item];
											smallFont.render(componentX + 1 + k6, componentY + 10 + j7,
													getShortenedAmountText(amount), 0);
											smallFont.render(componentX + k6, componentY + 9 + j7,
													getShortenedAmountText(amount), 0xffff00);
										}
									}
								}
							} else if (child.sprites != null && item < 20) {
								DirectSprite sprite = child.sprites[item];
								if (sprite != null) {
									sprite.drawSprite(componentX, componentY);
								}
							}
							item++;
						}
					}

				} else if (child.type == 3) {
					boolean flag = false;
					if (anInt1039 == child.id || anInt1048 == child.id || anInt1026 == child.id) {
						flag = true;
					}
					int colour;
					if (method131(child)) {
						colour = child.anInt219;
						if (flag && child.anInt239 != 0) {
							colour = child.anInt239;
						}
					} else {
						colour = child.colour;
						if (flag && child.anInt216 != 0) {
							colour = child.anInt216;
						}
					}

					if (child.alpha == 0) {
						if (child.filled) {
							Raster.fillRectangle(currentX, currentY, child.height, child.width, colour);
						} else {
							Raster.drawRectangle(currentX, currentY, child.width, child.height, colour);
						}
					} else if (child.filled) {
						Raster.method335(currentX, currentY, child.width, child.height, colour, 256 - (child.alpha & 0xff));
					} else {
						Raster.method338(currentX, currentY, child.width, child.height, colour, 256 - (child.alpha & 0xff));
					}
				} else if (child.type == 4) {
					Font font = child.font;
					String text = child.hiddenText;
					boolean flag1 = false;
					if (anInt1039 == child.id || anInt1048 == child.id || anInt1026 == child.id) {
						flag1 = true;
					}
					int colour;

					if (method131(child)) {
						colour = child.anInt219;
						if (flag1 && child.anInt239 != 0) {
							colour = child.anInt239;
						}
						if (child.text.length() > 0) {
							text = child.text;
						}
					} else {
						colour = child.colour;
						if (flag1 && child.anInt216 != 0) {
							colour = child.anInt216;
						}
					}
					if (child.anInt217 == 6 && aBoolean1149) {
						text = "Please wait...";
						colour = child.colour;
					}
					if (Raster.width == 479) {
						if (colour == 0xffff00) {
							colour = 255;
						}
						if (colour == 49152) {
							colour = 0xffffff;
						}
					}
					for (int l6 = currentY + font.verticalSpace; text.length() > 0; l6 += font.verticalSpace) {
						if (text.indexOf("%") != -1) {
							do {
								int index = text.indexOf("%1");
								if (index == -1) {
									break;
								}
								text = text.substring(0, index) + getDisplayableAmountString(executeScript(child, 0))
										+ text.substring(index + 2);
							} while (true);
							do {
								int index = text.indexOf("%2");
								if (index == -1) {
									break;
								}
								text = text.substring(0, index) + getDisplayableAmountString(executeScript(child, 1))
										+ text.substring(index + 2);
							} while (true);
							do {
								int index = text.indexOf("%3");
								if (index == -1) {
									break;
								}
								text = text.substring(0, index) + getDisplayableAmountString(executeScript(child, 2))
										+ text.substring(index + 2);
							} while (true);
							do {
								int index = text.indexOf("%4");
								if (index == -1) {
									break;
								}
								text = text.substring(0, index) + getDisplayableAmountString(executeScript(child, 3))
										+ text.substring(index + 2);
							} while (true);
							do {
								int index = text.indexOf("%5");
								if (index == -1) {
									break;
								}
								text = text.substring(0, index) + getDisplayableAmountString(executeScript(child, 4))
										+ text.substring(index + 2);
							} while (true);
						}
						int l8 = text.indexOf("\\n");
						String s1;
						if (l8 != -1) {
							s1 = text.substring(0, l8);
							text = text.substring(l8 + 2);
						} else {
							s1 = text;
							text = "";
						}
						if (child.centeredText) {
							font.shadowCentre(currentX + child.width / 2, l6, s1, child.shadowedText, colour);
						} else {
							font.shadow(currentX, l6, s1, child.shadowedText, colour);
						}
					}

				} else if (child.type == 5) {
					DirectSprite sprite;

					if (method131(child)) {
						sprite = child.aClass30_Sub2_Sub1_Sub1_260;
					} else {
						sprite = child.aClass30_Sub2_Sub1_Sub1_207;
					}

					if (sprite != null) {
						sprite.drawSprite(currentX, currentY);
					}
				} else if (child.type == 6) {
					int k3 = Rasterizer.originViewX;
					int j4 = Rasterizer.originViewY;
					Rasterizer.originViewX = currentX + child.width / 2;
					Rasterizer.originViewY = currentY + child.height / 2;
					int i5 = Rasterizer.SINE[child.spritePitch] * child.spriteScale >> 16;
					int l5 = Rasterizer.COSINE[child.spritePitch] * child.spriteScale >> 16;
					boolean flag2 = method131(child);
					int id;

					if (flag2) {
						id = child.anInt258;
					} else {
						id = child.mediaAnimationId;
					}

					Model model;
					if (id == -1) {
						model = child.method209(-1, -1, flag2);
					} else {
						Animation animation = Animation.animations[id];
						model = child.method209(animation.primaryFrames[child.displayedFrameCount],
								animation.secondaryFrames[child.displayedFrameCount], flag2);
					}
					if (model != null) {
						model.render(0, child.spriteRoll, 0, child.spritePitch, 0, i5, l5);
					}
					Rasterizer.originViewX = k3;
					Rasterizer.originViewY = j4;
				} else if (child.type == 7) {
					Font font = child.font;
					int slot = 0;
					for (int j5 = 0; j5 < child.height; j5++) {
						for (int i6 = 0; i6 < child.width; i6++) {
							if (child.inventoryIds[slot] > 0) {
								ItemDefinition definition = ItemDefinition.lookup(child.inventoryIds[slot] - 1);
								String name = definition.name;
								if (definition.stackable || child.inventoryAmounts[slot] != 1) {
									name = name + " x" + getFullAmountText(child.inventoryAmounts[slot]);
								}
								int i9 = currentX + i6 * (115 + child.spritePaddingX);
								int k9 = currentY + j5 * (12 + child.spritePaddingY);
								if (child.centeredText) {
									font.shadowCentre(i9 + child.width / 2, k9, name, child.shadowedText, child.colour);
								} else {
									font.shadow(i9, k9, name, child.shadowedText, child.colour);
								}
							}
							slot++;
						}
					}
				}
			}
		}

		Raster.setBounds(clipTop, clipLeft, clipRight, clipBottom);
	}

	public final void method106(IndexedImage image) {
		int j = 256;
		for (int k = 0; k < anIntArray1190.length; k++) {
			anIntArray1190[k] = 0;
		}

		for (int l = 0; l < 5000; l++) {
			int i1 = (int) (Math.random() * 128D * j);
			anIntArray1190[i1] = (int) (Math.random() * 256D);
		}

		for (int j1 = 0; j1 < 20; j1++) {
			for (int k1 = 1; k1 < j - 1; k1++) {
				for (int i2 = 1; i2 < 127; i2++) {
					int k2 = i2 + (k1 << 7);
					anIntArray1191[k2] = (anIntArray1190[k2 - 1] + anIntArray1190[k2 + 1] + anIntArray1190[k2 - 128] + anIntArray1190[k2 + 128]) / 4;
				}

			}

			int ai[] = anIntArray1190;
			anIntArray1190 = anIntArray1191;
			anIntArray1191 = ai;
		}

		if (image != null) {
			int l1 = 0;
			for (int j2 = 0; j2 < image.height; j2++) {
				for (int l2 = 0; l2 < image.width; l2++) {
					if (image.raster[l1++] != 0) {
						int i3 = l2 + 16 + image.drawOffsetX;
						int j3 = j2 + 16 + image.drawOffsetY;
						int k3 = i3 + (j3 << 7);
						anIntArray1190[k3] = 0;
					}
				}
			}
		}
	}

	public final void method108() {
		try {
			int j = localPlayer.worldX + anInt1278;
			int k = localPlayer.worldY + anInt1131;

			if (anInt1014 - j < -500 || anInt1014 - j > 500 || anInt1015 - k < -500 || anInt1015 - k > 500) {
				anInt1014 = j;
				anInt1015 = k;
			}

			if (anInt1014 != j) {
				anInt1014 += (j - anInt1014) / 16;
			}

			if (anInt1015 != k) {
				anInt1015 += (k - anInt1015) / 16;
			}

			if (super.keyStatuses[1] == 1) {
				anInt1186 += (-24 - anInt1186) / 2;
			} else if (super.keyStatuses[2] == 1) {
				anInt1186 += (24 - anInt1186) / 2;
			} else {
				anInt1186 /= 2;
			}

			if (super.keyStatuses[3] == 1) {
				anInt1187 += (12 - anInt1187) / 2;
			} else if (super.keyStatuses[4] == 1) {
				anInt1187 += (-12 - anInt1187) / 2;
			} else {
				anInt1187 /= 2;
			}
			cameraYaw = cameraYaw + anInt1186 / 2 & 0x7ff;
			cameraRoll += anInt1187 / 2;
			if (cameraRoll < 128) {
				cameraRoll = 128;
			}
			if (cameraRoll > 383) {
				cameraRoll = 383;
			}
			int l = anInt1014 >> 7;
			int i1 = anInt1015 >> 7;
			int j1 = method42(anInt1014, anInt1015, plane);
			int k1 = 0;
			if (l > 3 && i1 > 3 && l < 100 && i1 < 100) {
				for (int x = l - 4; x <= l + 4; x++) {
					for (int y = i1 - 4; y <= i1 + 4; y++) {
						int z = plane;
						if (z < 3 && (aByteArrayArrayArray1258[1][x][y] & 2) == 2) {
							z++;
						}
						int i3 = j1 - anIntArrayArrayArray1214[z][x][y];
						if (i3 > k1) {
							k1 = i3;
						}
					}
				}
			}

			anInt1005++;
			if (anInt1005 > 1512) {
				anInt1005 = 0;
				outgoing.writeOpcode(77);
				outgoing.writeByte(0);
				int i2 = outgoing.position;
				outgoing.writeByte((int) (Math.random() * 256D));
				outgoing.writeByte(101);
				outgoing.writeByte(233);
				outgoing.writeShort(45092);
				if ((int) (Math.random() * 2D) == 0) {
					outgoing.writeShort(35784);
				}
				outgoing.writeByte((int) (Math.random() * 256D));
				outgoing.writeByte(64);
				outgoing.writeByte(38);
				outgoing.writeShort((int) (Math.random() * 65536D));
				outgoing.writeShort((int) (Math.random() * 65536D));
				outgoing.writeSizeByte(outgoing.position - i2);
			}
			int j2 = k1 * 192;
			if (j2 > 0x17f00) {
				j2 = 0x17f00;
			}
			if (j2 < 32768) {
				j2 = 32768;
			}
			if (j2 > anInt984) {
				anInt984 += (j2 - anInt984) / 24;
				return;
			}
			if (j2 < anInt984) {
				anInt984 += (j2 - anInt984) / 80;
				return;
			}
		} catch (Exception _ex) {
			SignLink.reportError("glfc_ex " + localPlayer.worldX + "," + localPlayer.worldY + "," + anInt1014 + "," + anInt1015
					+ "," + sectorX + "," + sectorY + "," + regionBaseX + "," + regionBaseY);
			throw new RuntimeException("eek");
		}
	}

	public final void method112() {
		drawChatMessages();
		if (anInt917 == 1) {
			crosses[anInt916 / 100].drawSprite(anInt914 - 8 - 4, anInt915 - 8 - 4);
			anInt1142++;
			if (anInt1142 > 67) {
				anInt1142 = 0;
				outgoing.writeOpcode(78);
			}
		}
		if (anInt917 == 2) {
			crosses[4 + anInt916 / 100].drawSprite(anInt914 - 8 - 4, anInt915 - 8 - 4);
		}
		if (currentStatusInterface != -1) {
			processWidgetAnimations(currentStatusInterface, tickDelta);
			method105(0, 0, Widget.widgets[currentStatusInterface], 0);
		}
		if (openInterfaceId != -1) {
			processWidgetAnimations(openInterfaceId, tickDelta);
			method105(0, 0, Widget.widgets[openInterfaceId], 0);
		}
		checkTutorialIsland();
		if (!menuOpen) {
			method82(0);
			method125();
		} else if (anInt948 == 0) {
			method40();
		}
		if (multicombat == 1) {
			headIcons[1].drawSprite(472, 296);
		}
		if (displayFps) {
			char c = '\u01FB';
			int k = 20;
			int i1 = 0xffff00;
			if (super.fps < 15) {
				i1 = 0xff0000;
			}
			frameFont.renderLeft(c, k, "Fps:" + super.fps, i1);
			k += 15;
			Runtime runtime = Runtime.getRuntime();
			int memory = (int) ((runtime.totalMemory() - runtime.freeMemory()) / 1024);
			i1 = 0xffff00;
			if (memory > 0x2000000 && lowMemory) {
				i1 = 0xff0000;
			}
			frameFont.renderLeft(c, k, "Mem:" + memory + "k", 0xffff00);
			k += 15;
		}
		if (systemUpdateTime != 0) {
			int seconds = systemUpdateTime / 50;
			int minutes = seconds / 60;
			seconds %= 60;
			if (seconds < 10) {
				frameFont.render(4, 329, "System update in: " + minutes + ":0" + seconds, 0xffff00);
			} else {
				frameFont.render(4, 329, "System update in: " + minutes + ":" + seconds, 0xffff00);
			}
			anInt849++;
			if (anInt849 > 75) {
				anInt849 = 0;
				outgoing.writeOpcode(148);
			}
		}
	}

	public final void method116() {
		int optionWidth = boldFont.getTextWidth("Choose Option");
		for (int i = 0; i < menuActionRow; i++) {
			int actionWidth = boldFont.getTextWidth(menuActionTexts[i]);
			if (actionWidth > optionWidth) {
				optionWidth = actionWidth;
			}
		}

		optionWidth += 8;
		int l = 15 * menuActionRow + 21;
		if (super.lastClickX > 4 && super.lastClickY > 4 && super.lastClickX < 516 && super.lastClickY < 338) {
			int i1 = super.lastClickX - 4 - optionWidth / 2;
			if (i1 + optionWidth > 512) {
				i1 = 512 - optionWidth;
			}
			if (i1 < 0) {
				i1 = 0;
			}
			int l1 = super.lastClickY - 4;
			if (l1 + l > 334) {
				l1 = 334 - l;
			}
			if (l1 < 0) {
				l1 = 0;
			}
			menuOpen = true;
			anInt948 = 0;
			anInt949 = i1;
			anInt950 = l1;
			anInt951 = optionWidth;
			anInt952 = 15 * menuActionRow + 22;
		}
		if (super.lastClickX > 553 && super.lastClickY > 205 && super.lastClickX < 743 && super.lastClickY < 466) {
			int j1 = super.lastClickX - 553 - optionWidth / 2;
			if (j1 < 0) {
				j1 = 0;
			} else if (j1 + optionWidth > 190) {
				j1 = 190 - optionWidth;
			}
			int i2 = super.lastClickY - 205;
			if (i2 < 0) {
				i2 = 0;
			} else if (i2 + l > 261) {
				i2 = 261 - l;
			}
			menuOpen = true;
			anInt948 = 1;
			anInt949 = j1;
			anInt950 = i2;
			anInt951 = optionWidth;
			anInt952 = 15 * menuActionRow + 22;
		}
		if (super.lastClickX > 17 && super.lastClickY > 357 && super.lastClickX < 496 && super.lastClickY < 453) {
			int k1 = super.lastClickX - 17 - optionWidth / 2;
			if (k1 < 0) {
				k1 = 0;
			} else if (k1 + optionWidth > 479) {
				k1 = 479 - optionWidth;
			}
			int j2 = super.lastClickY - 357;
			if (j2 < 0) {
				j2 = 0;
			} else if (j2 + l > 96) {
				j2 = 96 - l;
			}
			menuOpen = true;
			anInt948 = 2;
			anInt949 = k1;
			anInt950 = j2;
			anInt951 = optionWidth;
			anInt952 = 15 * menuActionRow + 22;
		}
	}

	public final void method118() {
		aBoolean831 = false;
		while (aBoolean962) {
			aBoolean831 = false;
			try {
				Thread.sleep(50L);
			} catch (Exception ex) {
			}
		}
		titleBox = null;
		titleButton = null;
		runes = null;
		anIntArray850 = null;
		anIntArray851 = null;
		anIntArray852 = null;
		anIntArray853 = null;
		anIntArray1190 = null;
		anIntArray1191 = null;
		anIntArray828 = null;
		anIntArray829 = null;
		aClass30_Sub2_Sub1_Sub1_1201 = null;
		aClass30_Sub2_Sub1_Sub1_1202 = null;
	}

	public final int method120() {
		int height = 3;
		if (anInt861 < 310) {
			int x = anInt858 >> 7;
			int y = anInt860 >> 7;
			int localX = localPlayer.worldX >> 7;
			int localY = localPlayer.worldY >> 7;
			if ((aByteArrayArrayArray1258[plane][x][y] & 4) != 0) {
				height = plane;
			}
			int dx;
			if (localX > x) {
				dx = localX - x;
			} else {
				dx = x - localX;
			}
			int dy;
			if (localY > y) {
				dy = localY - y;
			} else {
				dy = y - localY;
			}
			if (dx > dy) {
				int i2 = dy * 0x10000 / dx;
				int k2 = 32768;
				while (x != localX) {
					if (x < localX) {
						x++;
					} else if (x > localX) {
						x--;
					}
					if ((aByteArrayArrayArray1258[plane][x][y] & 4) != 0) {
						height = plane;
					}
					k2 += i2;
					if (k2 >= 0x10000) {
						k2 -= 0x10000;
						if (y < localY) {
							y++;
						} else if (y > localY) {
							y--;
						}
						if ((aByteArrayArrayArray1258[plane][x][y] & 4) != 0) {
							height = plane;
						}
					}
				}
			} else {
				int j2 = dx * 0x10000 / dy;
				int l2 = 32768;
				while (y != localY) {
					if (y < localY) {
						y++;
					} else if (y > localY) {
						y--;
					}
					if ((aByteArrayArrayArray1258[plane][x][y] & 4) != 0) {
						height = plane;
					}
					l2 += j2;
					if (l2 >= 0x10000) {
						l2 -= 0x10000;
						if (x < localX) {
							x++;
						} else if (x > localX) {
							x--;
						}
						if ((aByteArrayArrayArray1258[plane][x][y] & 4) != 0) {
							height = plane;
						}
					}
				}
			}
		}
		if ((aByteArrayArrayArray1258[plane][localPlayer.worldX >> 7][localPlayer.worldY >> 7] & 4) != 0) {
			height = plane;
		}
		return height;
	}

	public final int method121() {
		int j = method42(anInt858, anInt860, plane);
		if (j - anInt859 < 800 && (aByteArrayArrayArray1258[plane][anInt858 >> 7][anInt860 >> 7] & 4) != 0) {
			return plane;
		}
		return 3;
	}

	public final void method125() {
		if (menuActionRow < 2 && selectedItemId == 0 && selectedSpellId == 0) {
			return;
		}
		String option;
		if (selectedItemId == 1 && menuActionRow < 2) {
			option = "Use " + selectedItemName + " with...";
		} else if (selectedSpellId == 1 && menuActionRow < 2) {
			option = selectedSpellName + "...";
		} else {
			option = menuActionTexts[menuActionRow - 1];
		}

		if (menuActionRow > 2) {
			option = option + "@whi@ / " + (menuActionRow - 2) + " more options";
		}
		boldFont.renderRandom(option, 4, 15, 0xffffff, true, tick / 1000);
	}

	public final void method126() {
		aClass15_1164.initializeRasterizer();
		if (minimapState == 2) {
			byte abyte0[] = mapBackground.raster;
			int[] ai = Raster.raster;
			int k2 = abyte0.length;
			for (int i5 = 0; i5 < k2; i5++) {
				if (abyte0[i5] == 0) {
					ai[i5] = 0;
				}
			}

			compass.method352(33, cameraYaw, anIntArray1057, 256, anIntArray968, 25, 0, 0, 33, 25);
			aClass15_1165.initializeRasterizer();
			return;
		}

		int i = cameraYaw + anInt1209 & 0x7ff;
		int j = 48 + localPlayer.worldX / 32;
		int l2 = 464 - localPlayer.worldY / 32;
		aClass30_Sub2_Sub1_Sub1_1263.method352(151, i, anIntArray1229, 256 + anInt1170, anIntArray1052, l2, 5, 25, 146, j);
		compass.method352(33, cameraYaw, anIntArray1057, 256, anIntArray968, 25, 0, 0, 33, 25);

		for (int j5 = 0; j5 < anInt1071; j5++) {
			int k = anIntArray1072[j5] * 4 + 2 - localPlayer.worldX / 32;
			int i3 = anIntArray1073[j5] * 4 + 2 - localPlayer.worldY / 32;
			drawOnMinimap(aClass30_Sub2_Sub1_Sub1Array1140[j5], k, i3);
		}

		for (int x = 0; x < 104; x++) {
			for (int y = 0; y < 104; y++) {
				Deque items = groundItems[plane][x][y];
				if (items != null) {
					int l = x * 4 + 2 - localPlayer.worldX / 32;
					int j3 = y * 4 + 2 - localPlayer.worldY / 32;
					drawOnMinimap(itemMapdot, l, j3);
				}
			}
		}

		for (int i6 = 0; i6 < npcCount; i6++) {
			Npc npc = npcs[npcList[i6]];
			if (npc != null && npc.isVisible()) {
				NpcDefinition definition = npc.definition;
				if (definition.morphisms != null) {
					definition = definition.morph();
				}
				if (definition != null && definition.drawMinimapDot && definition.clickable) {
					int x = npc.worldX / 32 - localPlayer.worldX / 32;
					int y = npc.worldY / 32 - localPlayer.worldY / 32;
					drawOnMinimap(npcMapdot, x, y);
				}
			}
		}

		for (int j6 = 0; j6 < playerCount; j6++) {
			Player player = players[playerList[j6]];
			if (player != null && player.isVisible()) {
				int x = player.worldX / 32 - localPlayer.worldX / 32;
				int y = player.worldY / 32 - localPlayer.worldY / 32;
				boolean friend = false;
				long username = StringUtils.encodeBase37(player.name);
				for (int index = 0; index < friendCount; index++) {
					if (username != friends[index] || friendWorlds[index] == 0) {
						continue;
					}

					friend = true;
					break;
				}

				boolean team = false;
				if (localPlayer.team != 0 && player.team != 0 && localPlayer.team == player.team) {
					team = true;
				}
				if (friend) {
					drawOnMinimap(friendMapdot, x, y);
				} else if (team) {
					drawOnMinimap(teamMapdot, x, y);
				} else {
					drawOnMinimap(playerMapdot, x, y);
				}
			}
		}

		if (headIconDrawType != 0 && tick % 20 < 10) {
			if (headIconDrawType == 1 && anInt1222 >= 0 && anInt1222 < npcs.length) {
				Npc npc = npcs[anInt1222];
				if (npc != null) {
					int k1 = npc.worldX / 32 - localPlayer.worldX / 32;
					int i4 = npc.worldY / 32 - localPlayer.worldY / 32;
					method81(secondMapmarker, i4, k1);
				}
			}
			if (headIconDrawType == 2) {
				int l1 = (anInt934 - regionBaseX) * 4 + 2 - localPlayer.worldX / 32;
				int j4 = (anInt935 - regionBaseY) * 4 + 2 - localPlayer.worldY / 32;
				method81(secondMapmarker, j4, l1);
			}
			if (headIconDrawType == 10 && lastInteractedWithPlayer >= 0 && lastInteractedWithPlayer < players.length) {
				Player player = players[lastInteractedWithPlayer];
				if (player != null) {
					int i2 = player.worldX / 32 - localPlayer.worldX / 32;
					int k4 = player.worldY / 32 - localPlayer.worldY / 32;
					method81(secondMapmarker, k4, i2);
				}
			}
		}
		if (destinationX != 0) {
			int j2 = destinationX * 4 + 2 - localPlayer.worldX / 32;
			int l4 = destinationY * 4 + 2 - localPlayer.worldY / 32;
			drawOnMinimap(firstMapmarker, j2, l4);
		}

		Raster.fillRectangle(97, 78, 3, 3, 0xffffff);
		aClass15_1165.initializeRasterizer();
	}

	public final void method127(Mob mob, int i) {
		method128(mob.worldX, i, mob.worldY);
	}

	public final void method128(int i, int j, int l) {
		if (i < 128 || l < 128 || i > 13056 || l > 13056) {
			spriteDrawX = -1;
			spriteDrawY = -1;
			return;
		}
		int i1 = method42(i, l, plane) - j;
		i -= anInt858;
		i1 -= anInt859;
		l -= anInt860;
		int j1 = Model.SINE[anInt861];
		int k1 = Model.COSINE[anInt861];
		int l1 = Model.SINE[anInt862];
		int i2 = Model.COSINE[anInt862];
		int j2 = l * l1 + i * i2 >> 16;
		l = l * i2 - i * l1 >> 16;
		i = j2;
		j2 = i1 * k1 - l * j1 >> 16;
		l = i1 * j1 + l * k1 >> 16;
		i1 = j2;
		if (l >= 50) {
			spriteDrawX = Rasterizer.originViewX + (i << 9) / l;
			spriteDrawY = Rasterizer.originViewY + (i1 << 9) / l;
		} else {
			spriteDrawX = -1;
			spriteDrawY = -1;
		}
	}

	public final void method129() {
		if (splitPrivateChat == 0) {
			return;
		}
		int messageSlot = 0;
		if (systemUpdateTime != 0) {
			messageSlot = 1;
		}
		for (int index = 0; index < 100; index++) {
			if (chatMessages[index] != null) {
				int type = chatTypes[index];
				String username = chatNames[index];
				if (username != null && username.startsWith("@cr1@")) {
					username = username.substring(5);
				}
				if (username != null && username.startsWith("@cr2@")) {
					username = username.substring(5);
				}
				if ((type == 3 || type == 7)
						&& (type == 7 || privateChatMode == 0 || privateChatMode == 1 && displayMessageFrom(username))) {
					int y = 329 - messageSlot * 13;
					if (super.mouseEventX > 4 && super.mouseEventY > y - 6 && super.mouseEventY <= y + 7) {
						int width = frameFont.getTextWidth("From:  " + username + chatMessages[index]) + 25;
						if (width > 450) {
							width = 450;
						}
						if (super.mouseEventX < 4 + width) {
							if (playerPrivelage >= 1) {
								menuActionTexts[menuActionRow] = "Report abuse @whi@" + username;
								menuActionIds[menuActionRow] = 2606;
								menuActionRow++;
							}
							menuActionTexts[menuActionRow] = "Add ignore @whi@" + username;
							menuActionIds[menuActionRow] = 2042;
							menuActionRow++;
							menuActionTexts[menuActionRow] = "Add friend @whi@" + username;
							menuActionIds[menuActionRow] = 2337;
							menuActionRow++;
						}
					}
					if (++messageSlot >= 5) {
						return;
					}
				}
				if ((type == 5 || type == 6) && privateChatMode < 2 && ++messageSlot >= 5) {
					return;
				}
			}
		}
	}

	public final boolean method131(Widget widget) {
		if (widget.scriptOperators == null) {
			return false;
		}

		for (int id = 0; id < widget.scriptOperators.length; id++) {
			int result = executeScript(widget, id);
			int k = widget.anIntArray212[id]; // expected result? default?
			int operator = widget.scriptOperators[id];

			if (operator == 1) {
				if (result != k) {
					return false;
				}
			} else if (operator == 2) {
				if (result >= k) {
					return false;
				}
			} else if (operator == 3) {
				if (result <= k) {
					return false;
				}
			} else if (operator == 4) {
				if (result == k) {
					return false;
				}
			}
		}

		return true;
	}

	public final void method133() {
		int c = 256;
		if (anInt1040 > 0) {
			for (int i = 0; i < 256; i++) {
				if (anInt1040 > 768) {
					anIntArray850[i] = method83(anIntArray851[i], anIntArray852[i], 1024 - anInt1040);
				} else if (anInt1040 > 256) {
					anIntArray850[i] = anIntArray852[i];
				} else {
					anIntArray850[i] = method83(anIntArray852[i], anIntArray851[i], 256 - anInt1040);
				}
			}

		} else if (anInt1041 > 0) {
			for (int j = 0; j < 256; j++) {
				if (anInt1041 > 768) {
					anIntArray850[j] = method83(anIntArray851[j], anIntArray853[j], 1024 - anInt1041);
				} else if (anInt1041 > 256) {
					anIntArray850[j] = anIntArray853[j];
				} else {
					anIntArray850[j] = method83(anIntArray853[j], anIntArray851[j], 256 - anInt1041);
				}
			}

		} else {
			for (int k = 0; k < 256; k++) {
				anIntArray850[k] = anIntArray851[k];
			}

		}
		for (int l = 0; l < 33920; l++) {
			aClass15_1110.pixels[l] = aClass30_Sub2_Sub1_Sub1_1201.raster[l];
		}

		int i1 = 0;
		int j1 = 1152;
		for (int k1 = 1; k1 < c - 1; k1++) {
			int l1 = anIntArray969[k1] * (c - k1) / c;
			int j2 = 22 + l1;
			if (j2 < 0) {
				j2 = 0;
			}
			i1 += j2;
			for (int l2 = j2; l2 < 128; l2++) {
				int j3 = anIntArray828[i1++];
				if (j3 != 0) {
					int l3 = j3;
					int j4 = 256 - j3;
					j3 = anIntArray850[j3];
					int l4 = aClass15_1110.pixels[j1];
					aClass15_1110.pixels[j1++] = ((j3 & 0xff00ff) * l3 + (l4 & 0xff00ff) * j4 & 0xff00ff00)
							+ ((j3 & 0xff00) * l3 + (l4 & 0xff00) * j4 & 0xff0000) >> 8;
				} else {
					j1++;
				}
			}

			j1 += j2;
		}

		aClass15_1110.drawImage(super.graphics, 0, 0);
		for (int i2 = 0; i2 < 33920; i2++) {
			aClass15_1111.pixels[i2] = aClass30_Sub2_Sub1_Sub1_1202.raster[i2];
		}

		i1 = 0;
		j1 = 1176;
		for (int k2 = 1; k2 < c - 1; k2++) {
			int i3 = anIntArray969[k2] * (c - k2) / c;
			int k3 = 103 - i3;
			j1 += i3;
			for (int i4 = 0; i4 < k3; i4++) {
				int k4 = anIntArray828[i1++];
				if (k4 != 0) {
					int i5 = k4;
					int j5 = 256 - k4;
					k4 = anIntArray850[k4];
					int k5 = aClass15_1111.pixels[j1];
					aClass15_1111.pixels[j1++] = ((k4 & 0xff00ff) * i5 + (k5 & 0xff00ff) * j5 & 0xff00ff00)
							+ ((k4 & 0xff00) * i5 + (k5 & 0xff00) * j5 & 0xff0000) >> 8;
				} else {
					j1++;
				}
			}

			i1 += 128 - k3;
			j1 += 128 - k3 - i3;
		}

		aClass15_1111.drawImage(super.graphics, 637, 0);
	}

	public final void method136() {
		aBoolean962 = true;
		try {
			long start = System.currentTimeMillis();
			int count = 0;
			int sleep = 20;

			while (aBoolean831) {
				flameTick++;
				method58();
				method58();
				method133();

				if (++count > 10) {
					long current = System.currentTimeMillis();
					int delta = (int) (current - start) / 10 - sleep;
					sleep = 40 - delta;
					if (sleep < 5) {
						sleep = 5;
					}
					count = 0;
					start = current;
				}
				try {
					Thread.sleep(sleep);
				} catch (Exception _ex) {
				}
			}
		} catch (Exception _ex) {
		}
		aBoolean962 = false;
	}

	public final void drawOnMinimap(DirectSprite sprite, int x, int y) {
		int k = cameraYaw + anInt1209 & 0x7ff;
		int r = x * x + y * y;
		if (r > 6400) {
			return;
		}

		int sin = Model.SINE[k];
		int cos = Model.COSINE[k];
		sin = sin * 256 / (anInt1170 + 256);
		cos = cos * 256 / (anInt1170 + 256);
		int k1 = y * sin + x * cos >> 16;
		int l1 = y * cos - x * sin >> 16;

		if (r > 2500) {
			sprite.method354(mapBackground, 83 - l1 - sprite.resizeHeight / 2 - 4, 94 + k1 - sprite.resizeWidth / 2 + 4);
		} else {
			sprite.drawSprite(94 + k1 - sprite.resizeWidth / 2 + 4, 83 - l1 - sprite.resizeHeight / 2 - 4);
		}
	}

	public final void method144(int j, int k, int l, int i1, int j1, int k1) {//Might be camera position?
		int l1 = 2048 - k & 0x7ff;
		int i2 = 2048 - j1 & 0x7ff;
		int j2 = 0;
		int k2 = 0;
		int l2 = j;
		if (l1 != 0) {
			int i3 = Model.SINE[l1];
			int k3 = Model.COSINE[l1];
			int i4 = k2 * k3 - l2 * i3 >> 16;
			l2 = k2 * i3 + l2 * k3 >> 16;
			k2 = i4;
		}
		if (i2 != 0) {
			int j3 = Model.SINE[i2];
			int l3 = Model.COSINE[i2];
			int j4 = l2 * j3 + j2 * l3 >> 16;
			l2 = l2 * l3 - j2 * j3 >> 16;
			j2 = j4;
		}
        anInt858 = l - j2;
		anInt859 = i1 - k2;
		anInt860 = k1 - l2;
		anInt861 = k;
		anInt862 = j1;
	}

	public final void method146() {
		anInt1265++;
		processPlayerAdditions(true);
		processNpcAdditions(true);
		processPlayerAdditions(false);
		processNpcAdditions(false);
		processProjectiles();
		processAnimableObjects();

		if (!oriented) {
			int i = cameraRoll;
			if (anInt984 / 256 > i) {
				i = anInt984 / 256;
			}
			if (aBooleanArray876[4] && anIntArray1203[4] + 128 > i) {
				i = anIntArray1203[4] + 128;
			}
			int k = cameraYaw + anInt896 & 0x7ff;
            if (frameHeight > Client.DEFAULT_HEIGHT || frameWidth > Client.DEFAULT_WIDTH) {
                int factor = (frameWidth - DEFAULT_WIDTH) + (frameHeight - DEFAULT_HEIGHT);
                factor /= 5.5; //TODO Put some kind of limit on this due to 4k monitors and such.
                //System.out.println(factor);
                int zoom = (600 + i * 3) - factor;
                method144(zoom, i, anInt1014, method42(localPlayer.worldX, localPlayer.worldY, plane) - 50, k, anInt1015);
            } else {
                method144(600 + i * 3, i, anInt1014, method42(localPlayer.worldX, localPlayer.worldY, plane) - 50, k, anInt1015);
            }
		}

		int j;
		if (!oriented) {
			j = method120();
		} else {
			j = method121();
		}

		int l = anInt858;
		int i1 = anInt859;
		int j1 = anInt860;
		int k1 = anInt861;
		int l1 = anInt862;
		for (int i2 = 0; i2 < 5; i2++) {
			if (aBooleanArray876[i2]) {
				int j2 = (int) (Math.random() * (anIntArray873[i2] * 2 + 1) - anIntArray873[i2] + Math.sin(anIntArray1030[i2]
						* (anIntArray928[i2] / 100D))
						* anIntArray1203[i2]);
				if (i2 == 0) {
					anInt858 += j2;
				}
				if (i2 == 1) {
					anInt859 += j2;
				}
				if (i2 == 2) {
					anInt860 += j2;
				}
				if (i2 == 3) {
					anInt862 = anInt862 + j2 & 0x7ff;
				}
				if (i2 == 4) {
					anInt861 += j2;
					if (anInt861 < 128) {
						anInt861 = 128;
					}
					if (anInt861 > 383) {
						anInt861 = 383;
					}
				}
			}
		}

		int k2 = Rasterizer.anInt1481;
		Model.aBoolean1684 = true;
		Model.anInt1687 = 0;
		Model.anInt1685 = super.mouseEventX - 4;
		Model.anInt1686 = super.mouseEventY - 4;
		Raster.reset();
		scene.method313(anInt858, anInt860, anInt862, anInt859, j, anInt861);
		scene.method288();
		updateCharacters();
		method61();
		method37(k2);
		method112();
		aClass15_1165.drawGameImage(super.graphics, 4, 4);
		anInt858 = l;
		anInt859 = i1;
		anInt860 = j1;
		anInt861 = k1;
		anInt862 = l1;
	}

	public final boolean method17(int j) {
		if (j < 0) {
			return false;
		}
		int k = menuActionIds[j];
		if (k >= 2000) {
			k -= 2000;
		}
		return k == 337;
	}

	public final void method18() {
		aClass15_1166.initializeRasterizer();
		Rasterizer.scanOffsets = anIntArray1180;
		chatBackground.draw(0, 0);
		if (messagePromptRaised) {
			boldFont.renderCentre(239, 40, aString1121, 0);
			boldFont.renderCentre(239, 60, messagePromptInput + "*", 128);
		} else if (inputDialogueState == 1) {
			boldFont.renderCentre(239, 40, "Enter amount:", 0);
			boldFont.renderCentre(239, 60, textInput + "*", 128);
		} else if (inputDialogueState == 2) {
			boldFont.renderCentre(239, 40, "Enter name:", 0);
			boldFont.renderCentre(239, 60, textInput + "*", 128);
		} else if (clickToContinueString != null) {
			boldFont.renderCentre(239, 40, clickToContinueString, 0);
			boldFont.renderCentre(239, 60, "Click to continue", 128);
		} else if (backDialogueId != -1) {
			method105(0, 0, Widget.widgets[backDialogueId], 0);
		} else if (dialogueId != -1) {
			method105(0, 0, Widget.widgets[dialogueId], 0);
		} else {
			Font typeface = frameFont;
			int count = 0;
			Raster.setBounds(77, 0, 463, 0);
			for (int message = 0; message < 100; message++) {
				if (chatMessages[message] != null) {
					int type = chatTypes[message];
					int y = 70 - count * 14 + chatboxScrollerPos;
					String username = chatNames[message];
					byte privilege = 0;
					if (username != null && username.startsWith("@cr1@")) {
						username = username.substring(5);
						privilege = 1;
					}
					if (username != null && username.startsWith("@cr2@")) {
						username = username.substring(5);
						privilege = 2;
					}
					if (type == 0) {
						if (y > 0 && y < 110) {
							typeface.render(4, y, chatMessages[message], 0);
						}
						count++;
					}
					if ((type == 1 || type == 2)
							&& (type == 1 || publicChatMode == 0 || publicChatMode == 1 && displayMessageFrom(username))) {
						if (y > 0 && y < 110) {
							int x = 4;
							if (privilege == 1) {
								modIcons[0].draw(x, y - 12);
								x += 14;
							}
							if (privilege == 2) {
								modIcons[1].draw(x, y - 12);
								x += 14;
							}
							typeface.render(x, y, username + ":", 0);
							x += typeface.getTextWidth(username) + 8;
							typeface.render(x, y, chatMessages[message], 255);
						}
						count++;
					}
					if ((type == 3 || type == 7) && splitPrivateChat == 0
							&& (type == 7 || privateChatMode == 0 || privateChatMode == 1 && displayMessageFrom(username))) {
						if (y > 0 && y < 110) {
							int k1 = 4;
							typeface.render(k1, y, "From", 0);
							k1 += typeface.getTextWidth("From ");
							if (privilege == 1) {
								modIcons[0].draw(k1, y - 12);
								k1 += 14;
							}
							if (privilege == 2) {
								modIcons[1].draw(k1, y - 12);
								k1 += 14;
							}
							typeface.render(k1, y, username + ":", 0);
							k1 += typeface.getTextWidth(username) + 8;
							typeface.render(k1, y, chatMessages[message], 0x800000);
						}
						count++;
					}
					if (type == 4 && (tradeChatMode == 0 || tradeChatMode == 1 && displayMessageFrom(username))) {
						if (y > 0 && y < 110) {
							typeface.render(4, y, username + " " + chatMessages[message], 0x800080);
						}
						count++;
					}
					if (type == 5 && splitPrivateChat == 0 && privateChatMode < 2) {
						if (y > 0 && y < 110) {
							typeface.render(4, y, chatMessages[message], 0x800000);
						}
						count++;
					}
					if (type == 6 && splitPrivateChat == 0 && privateChatMode < 2) {
						if (y > 0 && y < 110) {
							typeface.render(4, y, "To " + username + ":", 0);
							typeface.render(12 + typeface.getTextWidth("To " + username), y, chatMessages[message], 0x800000);
						}
						count++;
					}
					if (type == 8 && (tradeChatMode == 0 || tradeChatMode == 1 && displayMessageFrom(username))) {
						if (y > 0 && y < 110) {
							typeface.render(4, y, username + " " + chatMessages[message], 0x7e3200);
						}
						count++;
					}
				}
			}

			Raster.setDefaultBounds();
			anInt1211 = count * 14 + 7;
			if (anInt1211 < 78) {
				anInt1211 = 78;
			}
			method30(77, anInt1211 - chatboxScrollerPos - 77, 0, 463, anInt1211);
			String s;
			if (localPlayer != null && localPlayer.name != null) {
				s = localPlayer.name;
			} else {
				s = StringUtils.format(username);
			}
			typeface.render(4, 90, s + ":", 0);
			typeface.render(6 + typeface.getTextWidth(s + ": "), 90, input + "*", 255);
			Raster.drawHorizontal(0, 77, 479, 0);
		}
		if (menuOpen && anInt948 == 2) {
			method40();
		}
		aClass15_1166.drawImage(super.graphics, 17, 357);
		aClass15_1165.initializeRasterizer();
		Rasterizer.scanOffsets = anIntArray1182;
	}

	public final void method20() {
		if (anInt1086 != 0) {
			return;
		}
		int j = super.lastMetaModifier;
		if (selectedSpellId == 1 && super.lastClickX >= 516 && super.lastClickY >= 160 && super.lastClickX <= 765
				&& super.lastClickY <= 205) {
			j = 0;
		}
		if (menuOpen) {
			if (j != 1) {
				int k = super.mouseEventX;
				int j1 = super.mouseEventY;
				if (anInt948 == 0) {
					k -= 4;
					j1 -= 4;
				}
				if (anInt948 == 1) {
					k -= 553;
					j1 -= 205;
				}
				if (anInt948 == 2) {
					k -= 17;
					j1 -= 357;
				}
				if (k < anInt949 - 10 || k > anInt949 + anInt951 + 10 || j1 < anInt950 - 10 || j1 > anInt950 + anInt952 + 10) {
					menuOpen = false;
					if (anInt948 == 1) {
						redrawTabArea = true;
					}
					if (anInt948 == 2) {
						redrawChatbox = true;
					}
				}
			}
			if (j == 1) {
				int l = anInt949;
				int k1 = anInt950;
				int i2 = anInt951;
				int k2 = super.lastClickX;
				int l2 = super.lastClickY;
				if (anInt948 == 0) {
					k2 -= 4;
					l2 -= 4;
				}
				if (anInt948 == 1) {
					k2 -= 553;
					l2 -= 205;
				}
				if (anInt948 == 2) {
					k2 -= 17;
					l2 -= 357;
				}
				int i3 = -1;
				for (int j3 = 0; j3 < menuActionRow; j3++) {
					int k3 = k1 + 31 + (menuActionRow - 1 - j3) * 15;
					if (k2 > l && k2 < l + i2 && l2 > k3 - 13 && l2 < k3 + 3) {
						i3 = j3;
					}
				}

				if (i3 != -1) {
					method69(i3);
				}
				menuOpen = false;
				if (anInt948 == 1) {
					redrawTabArea = true;
				}
				if (anInt948 == 2) {
					redrawChatbox = true;
					return;
				}
			}
		} else {
			if (j == 1 && menuActionRow > 0) {
				int i1 = menuActionIds[menuActionRow - 1];
				if (i1 == 632 || i1 == 78 || i1 == 867 || i1 == 431 || i1 == 53 || i1 == 74 || i1 == 454 || i1 == 539
						|| i1 == 493 || i1 == 847 || i1 == 447 || i1 == 1125) {
					int l1 = anIntArray1091[menuActionRow - 1];
					int id = anIntArray1092[menuActionRow - 1];
					Widget widget = Widget.widgets[id];
					if (widget.aBoolean259 || widget.aBoolean235) {
						aBoolean1242 = false;
						anInt989 = 0;
						anInt1084 = id;
						anInt1085 = l1;
						anInt1086 = 2;
						anInt1087 = super.lastClickX;
						anInt1088 = super.lastClickY;
						if (Widget.widgets[id].parent == openInterfaceId) {
							anInt1086 = 1;
						}
						if (Widget.widgets[id].parent == backDialogueId) {
							anInt1086 = 3;
						}
						return;
					}
				}
			}
			if (j == 1 && (mouseButtons == 1 || method17(menuActionRow - 1)) && menuActionRow > 2) {
				j = 2;
			}
			if (j == 1 && menuActionRow > 0) {
				method69(menuActionRow - 1);
			}
			if (j == 2 && menuActionRow > 0) {
				method116();
			}
		}
	}

	public final void method22() {
		try {
			anInt985 = -1;
			incompleteAnimables.clear();
			projectiles.clear();
			Rasterizer.method366();
			unlinkCaches();
			scene.reset();
			System.gc();
			for (int i = 0; i < 4; i++) {
				collisionMaps[i].init();
			}

			for (int z = 0; z < 4; z++) {
				for (int x = 0; x < 104; x++) {
					for (int y = 0; y < 104; y++) {
						aByteArrayArrayArray1258[z][x][y] = 0;
					}
				}
			}

			Region region = new Region(aByteArrayArrayArray1258, 104, 104, anIntArrayArrayArray1214);
			int count = localRegionMapData.length;
			outgoing.writeOpcode(0);

			if (!constructedViewport) {
				for (int i = 0; i < count; i++) {
					int dX = (localRegionIds[i] >> 8) * 64 - regionBaseX;
					int dY = (localRegionIds[i] & 0xff) * 64 - regionBaseY;
					byte[] data = localRegionMapData[i];
					if (data != null) {
						region.decodeRegionMapData(data, dY, dX, (sectorX - 6) * 8, (sectorY - 6) * 8, collisionMaps);
					}
				}

				for (int i = 0; i < count; i++) {
					int x = (localRegionIds[i] >> 8) * 64 - regionBaseX;
					int y = (localRegionIds[i] & 0xff) * 64 - regionBaseY;
					byte[] data = localRegionMapData[i];
					if (data == null && sectorY < 800) {
						region.method174(x, y, 64, 64);
					}
				}

				anInt1097++;
				if (anInt1097 > 160) {
					anInt1097 = 0;
					outgoing.writeOpcode(238);
					outgoing.writeByte(96);
				}

				outgoing.writeOpcode(0);
				for (int i = 0; i < count; i++) {
					byte[] landscape = localRegionLandscapeData[i];
					if (landscape != null) {
						int x = (localRegionIds[i] >> 8) * 64 - regionBaseX;
						int y = (localRegionIds[i] & 0xff) * 64 - regionBaseY;
						region.decodeRegionLandscapes(collisionMaps, scene, landscape, x, y);
					}
				}
			}

			if (constructedViewport) {
				for (int z = 0; z < 4; z++) {
					for (int x = 0; x < 13; x++) {
						for (int y = 0; y < 13; y++) {
							int data = localSectors[z][x][y];

							if (data != -1) {
								int plane = data >> 24 & 3;
								int rotation = data >> 1 & 3;
								int secX = data >> 14 & 0x3ff;
								int secY = data >> 3 & 0x7ff;
								int regionId = (secX / 8 << 8) + secY / 8;

								for (int index = 0; index < localRegionIds.length; index++) {
									if (localRegionIds[index] != regionId || localRegionMapData[index] == null) {
										continue;
									}
									region.decodeConstructedMapData(plane, rotation, collisionMaps, x * 8, (secX & 7) * 8,
											localRegionMapData[index], (secY & 7) * 8, z, y * 8);
									break;
								}
							}
						}
					}
				}

				for (int x = 0; x < 13; x++) {
					for (int y = 0; y < 13; y++) {
						int sector = localSectors[0][x][y];
						if (sector == -1) {
							region.method174(x * 8, y * 8, 8, 8);
						}
					}
				}

				outgoing.writeOpcode(0);
				for (int plane = 0; plane < 4; plane++) {
					for (int sectorX = 0; sectorX < 13; sectorX++) {
						for (int sectorY = 0; sectorY < 13; sectorY++) {
							int sector = localSectors[plane][sectorX][sectorY];

							if (sector != -1) {
								int k10 = sector >> 24 & 3;
								int orientation = sector >> 1 & 3;
								int currSectorX = sector >> 14 & 0x3ff;
								int currSectorY = sector >> 3 & 0x7ff;
								int regionId = (currSectorX / 8 << 8) + currSectorY / 8;

								for (int index = 0; index < localRegionIds.length; index++) {
									if (localRegionIds[index] != regionId || localRegionLandscapeData[index] == null) {
										continue;
									}

									region.decodeConstructedLocations(collisionMaps, scene, k10, sectorX * 8,
											(currSectorY & 7) * 8, plane, localRegionLandscapeData[index], (currSectorX & 7) * 8,
											orientation, sectorY * 8);
									break;
								}
							}
						}
					}
				}
			}

			outgoing.writeOpcode(0);
			region.method171(collisionMaps, scene);
			aClass15_1165.initializeRasterizer();
			outgoing.writeOpcode(0);

			int k3 = Region.anInt145;
			if (k3 > plane) {
				k3 = plane;
			} else if (k3 < plane - 1) {
				k3 = plane - 1;
			}

			if (lowMemory) {
				scene.fill(Region.anInt145);
			} else {
				scene.fill(0);
			}

			for (int x = 0; x < 104; x++) {
				for (int y = 0; y < 104; y++) {
					processGroundItems(x, y);
				}
			}

			anInt1051++;
			if (anInt1051 > 98) {
				anInt1051 = 0;
				outgoing.writeOpcode(150);
			}
			method63();
		} catch (Exception exception) {
		}

		ObjectDefinition.baseModels.unlink();
		if (super.frame != null) {
			outgoing.writeOpcode(210);
			outgoing.writeInt(0x3f008edd);
		}

		if (lowMemory && SignLink.cache != null) {
			int modelCount = provider.getCount(0);
			for (int id = 0; id < modelCount; id++) {
				int attributes = provider.getModelAttributes(id);
				if ((attributes & 0x79) == 0) {
					Model.clear(id);
				}
			}

		}

		System.gc();
		Rasterizer.method367(20);
		provider.clearExtras();

		int minX = (sectorX - 6) / 8 - 1;
		int maxX = (sectorX + 6) / 8 + 1;
		int minY = (sectorY - 6) / 8 - 1;
		int maxY = (sectorY + 6) / 8 + 1;
		if (inPlayerOwnedHouse) {
			minX = minY = 49;
			maxX = maxY = 50;
		}

		for (int regionX = minX; regionX <= maxX; regionX++) {
			for (int regionY = minY; regionY <= maxY; regionY++) {
				if (regionX == minX || regionX == maxX || regionY == minY || regionY == maxY) {
					int map = provider.resolve(regionX, regionY, 0);
					if (map != -1) {
						provider.loadExtra(3, map);
					}

					int landscape = provider.resolve(regionX, regionY, 1);
					if (landscape != -1) {
						provider.loadExtra(3, landscape);
					}
				}
			}
		}
	}

	public final void method24(int plane) {
		int raster[] = aClass30_Sub2_Sub1_Sub1_1263.raster;
		int pixels = raster.length;
		for (int i = 0; i < pixels; i++) {
			raster[i] = 0;
		}

		for (int y = 1; y < 103; y++) {
			int i1 = 24628 + (103 - y) * 512 * 4;
			for (int x = 1; x < 103; x++) {
				if ((aByteArrayArrayArray1258[plane][x][y] & 0x18) == 0) {
					scene.method309(raster, x, y, plane, i1, 512);
				}

				if (plane < 3 && (aByteArrayArrayArray1258[plane + 1][x][y] & 8) != 0) {
					scene.method309(raster, x, y, plane + 1, i1, 512);
				}
				i1 += 4;
			}
		}

		int j1 = (238 + (int) (Math.random() * 20) - 10 << 16) + (238 + (int) (Math.random() * 20) - 10 << 8) + 238
				+ (int) (Math.random() * 20) - 10;
		int l1 = 238 + (int) (Math.random() * 20) - 10 << 16;
		aClass30_Sub2_Sub1_Sub1_1263.initRaster();

		for (int y = 1; y < 103; y++) {
			for (int x = 1; x < 103; x++) {
				if ((aByteArrayArrayArray1258[plane][x][y] & 0x18) == 0) {
					method50(x, y, plane, j1, l1);
				}
				if (plane < 3 && (aByteArrayArrayArray1258[plane + 1][x][y] & 8) != 0) {
					method50(x, y, plane + 1, j1, l1);
				}
			}
		}

		aClass15_1165.initializeRasterizer();
		anInt1071 = 0;
		for (int x = 0; x < 104; x++) {
			for (int y = 0; y < 104; y++) {
				int id = scene.getFloorDecorationKey(x, y, this.plane);
				if (id != 0) {
					id = id >> 14 & 0x7fff;
					int function = ObjectDefinition.lookup(id).minimapFunction;
					if (function >= 0) {
						int viewportX = x;
						int viewportY = y;
						if (function != 22 && function != 29 && function != 34 && function != 36 && function != 46
								&& function != 47 && function != 48) {
							byte maxX = 104;
							byte maxY = 104;
							int[][] adjacencies = collisionMaps[this.plane].adjacencies;
							for (int i4 = 0; i4 < 10; i4++) {
								int j4 = (int) (Math.random() * 4);
								if (j4 == 0 && viewportX > 0 && viewportX > x - 3
										&& (adjacencies[viewportX - 1][viewportY] & 0x1280108) == 0) {
									viewportX--;
								}
								if (j4 == 1 && viewportX < maxX - 1 && viewportX < x + 3
										&& (adjacencies[viewportX + 1][viewportY] & 0x1280180) == 0) {
									viewportX++;
								}
								if (j4 == 2 && viewportY > 0 && viewportY > y - 3
										&& (adjacencies[viewportX][viewportY - 1] & 0x1280102) == 0) {
									viewportY--;
								}
								if (j4 == 3 && viewportY < maxY - 1 && viewportY < y + 3
										&& (adjacencies[viewportX][viewportY + 1] & 0x1280120) == 0) {
									viewportY++;
								}
							}
						}
						aClass30_Sub2_Sub1_Sub1Array1140[anInt1071] = mapFunctions[function];
						anIntArray1072[anInt1071] = viewportX;
						anIntArray1073[anInt1071] = viewportY;
						anInt1071++;
					}
				}
			}
		}
	}

	public final void method29(int i, Widget widget, int k, int l, int i1, int j1) {
		if (widget.type != 0 || widget.children == null || widget.aBoolean266) {
			return;
		}
		if (k < i || i1 < l || k > i + widget.width || i1 > l + widget.height) {
			return;
		}
		int childCount = widget.children.length;
		for (int childIndex = 0; childIndex < childCount; childIndex++) {
			int i2 = widget.childX[childIndex] + i;
			int j2 = widget.childY[childIndex] + l - j1;
			Widget child = Widget.widgets[widget.children[childIndex]];
			i2 += child.horizontalDrawOffset;
			j2 += child.verticalDrawOffset;
			if ((child.anInt230 >= 0 || child.anInt216 != 0) && k >= i2 && i1 >= j2 && k < i2 + child.width
					&& i1 < j2 + child.height) {
				if (child.anInt230 >= 0) {
					anInt886 = child.anInt230;
				} else {
					anInt886 = child.id;
				}
			}
			if (child.type == 0) {
				method29(i2, child, k, j2, i1, child.scrollPosition);
				if (child.scrollLimit > child.height) {
					method65(i2 + child.width, child.height, k, i1, child, j2, true, child.scrollLimit, 0);
				}
			} else {
				if (child.anInt217 == 1 && k >= i2 && i1 >= j2 && k < i2 + child.width && i1 < j2 + child.height) {
					boolean flag = false;
					if (child.anInt214 != 0) {
						flag = method103(child);
					}
					if (!flag) {
						menuActionTexts[menuActionRow] = child.hover;
						menuActionIds[menuActionRow] = 315;
						anIntArray1092[menuActionRow] = child.id;
						menuActionRow++;
					}
				}
				if (child.anInt217 == 2 && selectedSpellId == 0 && k >= i2 && i1 >= j2 && k < i2 + child.width
						&& i1 < j2 + child.height) {
					String s = child.aString222;
					if (s.indexOf(" ") != -1) {
						s = s.substring(0, s.indexOf(" "));
					}
					menuActionTexts[menuActionRow] = s + " @gre@" + child.aString218;
					menuActionIds[menuActionRow] = 626;
					anIntArray1092[menuActionRow] = child.id;
					menuActionRow++;
				}
				if (child.anInt217 == 3 && k >= i2 && i1 >= j2 && k < i2 + child.width && i1 < j2 + child.height) {
					menuActionTexts[menuActionRow] = "Close";
					menuActionIds[menuActionRow] = 200;
					anIntArray1092[menuActionRow] = child.id;
					menuActionRow++;
				}
				if (child.anInt217 == 4 && k >= i2 && i1 >= j2 && k < i2 + child.width && i1 < j2 + child.height) {
					menuActionTexts[menuActionRow] = child.hover;
					menuActionIds[menuActionRow] = 169;
					anIntArray1092[menuActionRow] = child.id;
					menuActionRow++;
				}
				if (child.anInt217 == 5 && k >= i2 && i1 >= j2 && k < i2 + child.width && i1 < j2 + child.height) {
					menuActionTexts[menuActionRow] = child.hover;
					menuActionIds[menuActionRow] = 646;
					anIntArray1092[menuActionRow] = child.id;
					menuActionRow++;
				}
				if (child.anInt217 == 6 && !aBoolean1149 && k >= i2 && i1 >= j2 && k < i2 + child.width && i1 < j2 + child.height) {
					menuActionTexts[menuActionRow] = child.hover;
					menuActionIds[menuActionRow] = 679;
					anIntArray1092[menuActionRow] = child.id;
					menuActionRow++;
				}
				if (child.type == 2) {
					int k2 = 0;
					for (int l2 = 0; l2 < child.height; l2++) {
						for (int i3 = 0; i3 < child.width; i3++) {
							int j3 = i2 + i3 * (32 + child.spritePaddingX);
							int k3 = j2 + l2 * (32 + child.spritePaddingY);
							if (k2 < 20) {
								j3 += child.spriteX[k2];
								k3 += child.spriteY[k2];
							}
							if (k >= j3 && i1 >= k3 && k < j3 + 32 && i1 < k3 + 32) {
								anInt1066 = k2;
								anInt1067 = child.id;
								if (child.inventoryIds[k2] > 0) {
									ItemDefinition definition = ItemDefinition.lookup(child.inventoryIds[k2] - 1);
									if (selectedItemId == 1 && child.aBoolean249) {
										if (child.id != anInt1284 || k2 != anInt1283) {
											menuActionTexts[menuActionRow] = "Use " + selectedItemName + " with @lre@"
													+ definition.name;
											menuActionIds[menuActionRow] = 870;
											anIntArray1094[menuActionRow] = definition.id;
											anIntArray1091[menuActionRow] = k2;
											anIntArray1092[menuActionRow] = child.id;
											menuActionRow++;
										}
									} else if (selectedSpellId == 1 && child.aBoolean249) {
										if ((anInt1138 & 0x10) == 16) {
											menuActionTexts[menuActionRow] = selectedSpellName + " @lre@" + definition.name;
											menuActionIds[menuActionRow] = 543;
											anIntArray1094[menuActionRow] = definition.id;
											anIntArray1091[menuActionRow] = k2;
											anIntArray1092[menuActionRow] = child.id;
											menuActionRow++;
										}
									} else {
										if (child.aBoolean249) {
											for (int l3 = 4; l3 >= 3; l3--) {
												if (definition.inventoryMenuActions != null
														&& definition.inventoryMenuActions[l3] != null) {
													menuActionTexts[menuActionRow] = definition.inventoryMenuActions[l3]
															+ " @lre@" + definition.name;
													if (l3 == 3) {
														menuActionIds[menuActionRow] = 493;
													}
													if (l3 == 4) {
														menuActionIds[menuActionRow] = 847;
													}
													anIntArray1094[menuActionRow] = definition.id;
													anIntArray1091[menuActionRow] = k2;
													anIntArray1092[menuActionRow] = child.id;
													menuActionRow++;
												} else if (l3 == 4) {
													menuActionTexts[menuActionRow] = "Drop @lre@" + definition.name;
													menuActionIds[menuActionRow] = 847;
													anIntArray1094[menuActionRow] = definition.id;
													anIntArray1091[menuActionRow] = k2;
													anIntArray1092[menuActionRow] = child.id;
													menuActionRow++;
												}
											}

										}
										if (child.aBoolean242) {
											menuActionTexts[menuActionRow] = "Use @lre@" + definition.name;
											menuActionIds[menuActionRow] = 447;
											anIntArray1094[menuActionRow] = definition.id;
											anIntArray1091[menuActionRow] = k2;
											anIntArray1092[menuActionRow] = child.id;
											menuActionRow++;
										}
										if (child.aBoolean249 && definition.inventoryMenuActions != null) {
											for (int i4 = 2; i4 >= 0; i4--) {
												if (definition.inventoryMenuActions[i4] != null) {
													menuActionTexts[menuActionRow] = definition.inventoryMenuActions[i4]
															+ " @lre@" + definition.name;
													if (i4 == 0) {
														menuActionIds[menuActionRow] = 74;
													}
													if (i4 == 1) {
														menuActionIds[menuActionRow] = 454;
													}
													if (i4 == 2) {
														menuActionIds[menuActionRow] = 539;
													}
													anIntArray1094[menuActionRow] = definition.id;
													anIntArray1091[menuActionRow] = k2;
													anIntArray1092[menuActionRow] = child.id;
													menuActionRow++;
												}
											}

										}
										if (child.actions != null) {
											for (int j4 = 4; j4 >= 0; j4--) {
												if (child.actions[j4] != null) {
													menuActionTexts[menuActionRow] = child.actions[j4] + " @lre@"
															+ definition.name;
													if (j4 == 0) {
														menuActionIds[menuActionRow] = 632;
													}
													if (j4 == 1) {
														menuActionIds[menuActionRow] = 78;
													}
													if (j4 == 2) {
														menuActionIds[menuActionRow] = 867;
													}
													if (j4 == 3) {
														menuActionIds[menuActionRow] = 431;
													}
													if (j4 == 4) {
														menuActionIds[menuActionRow] = 53;
													}
													anIntArray1094[menuActionRow] = definition.id;
													anIntArray1091[menuActionRow] = k2;
													anIntArray1092[menuActionRow] = child.id;
													menuActionRow++;
												}
											}

										}
										menuActionTexts[menuActionRow] = "Examine @lre@" + definition.name;
										menuActionIds[menuActionRow] = 1125;
										anIntArray1094[menuActionRow] = definition.id;
										anIntArray1091[menuActionRow] = k2;
										anIntArray1092[menuActionRow] = child.id;
										menuActionRow++;
									}
								}
							}
							k2++;
						}
					}
				}
			}
		}
	}

	public final void method30(int height, int k, int y, int x, int j1) {
		aClass30_Sub2_Sub1_Sub2_1024.draw(x, y);
		aClass30_Sub2_Sub1_Sub2_1025.draw(x, y + height - 16);
		Raster.fillRectangle(x, y + 16, height - 32, 16, anInt1002);
		int k1 = (height - 32) * height / j1;
		if (k1 < 8) {
			k1 = 8;
		}
		int l1 = (height - 32 - k1) * k / (j1 - height);
		Raster.fillRectangle(x, y + 16 + l1, k1, 16, anInt1063);
		Raster.drawVertical(x, y + 16 + l1, k1, anInt902);
		Raster.drawVertical(x + 1, y + 16 + l1, k1, anInt902);
		Raster.drawHorizontal(x, y + 16 + l1, 16, anInt902);
		Raster.drawHorizontal(x, y + 17 + l1, 16, anInt902);
		Raster.drawVertical(x + 15, y + 16 + l1, k1, anInt927);
		Raster.drawVertical(x + 14, y + 17 + l1, k1 - 1, anInt927);
		Raster.drawHorizontal(x, y + 15 + l1 + k1, 16, anInt927);
		Raster.drawHorizontal(x + 1, y + 14 + l1 + k1, 15, anInt927);
	}

	public final void method32() {
		if (super.lastMetaModifier == 1) {
			if (super.lastClickX >= 6 && super.lastClickX <= 106 && super.lastClickY >= 467 && super.lastClickY <= 499) {
				publicChatMode = (publicChatMode + 1) % 4;
				aBoolean1233 = true;
				outgoing.writeOpcode(95);
				outgoing.writeByte(publicChatMode);
				outgoing.writeByte(privateChatMode);
				outgoing.writeByte(tradeChatMode);
			}
			if (super.lastClickX >= 135 && super.lastClickX <= 235 && super.lastClickY >= 467 && super.lastClickY <= 499) {
				privateChatMode = (privateChatMode + 1) % 3;
				aBoolean1233 = true;
				outgoing.writeOpcode(95);
				outgoing.writeByte(publicChatMode);
				outgoing.writeByte(privateChatMode);
				outgoing.writeByte(tradeChatMode);
			}
			if (super.lastClickX >= 273 && super.lastClickX <= 373 && super.lastClickY >= 467 && super.lastClickY <= 499) {
				tradeChatMode = (tradeChatMode + 1) % 3;
				aBoolean1233 = true;
				outgoing.writeOpcode(95);
				outgoing.writeByte(publicChatMode);
				outgoing.writeByte(privateChatMode);
				outgoing.writeByte(tradeChatMode);
			}
			if (super.lastClickX >= 412 && super.lastClickX <= 512 && super.lastClickY >= 467 && super.lastClickY <= 499) {
				if (openInterfaceId == -1) {
					clearTopInterfaces();
					reportInput = "";
					reportAbuseMuteToggle = false;
					for (Widget widget : Widget.widgets) {
						if (widget == null || widget.anInt214 != 600) {
							continue;
						}
						reportInterfaceId = openInterfaceId = widget.parent;
						break;
					}

				} else {
					addChatMessage(0, "Please close the interface you have open before using 'report abuse'", "");
				}
			}
			anInt940++;
			if (anInt940 > 1386) {
				anInt940 = 0;
				outgoing.writeOpcode(165);
				outgoing.writeByte(0);
				int start = outgoing.position;
				outgoing.writeByte(139);
				outgoing.writeByte(150);
				outgoing.writeShort(32131);
				outgoing.writeByte((int) (Math.random() * 256));
				outgoing.writeShort(3250);
				outgoing.writeByte(177);
				outgoing.writeShort(24859);
				outgoing.writeByte(119);
				if ((int) (Math.random() * 2) == 0) {
					outgoing.writeShort(47234);
				}
				if ((int) (Math.random() * 2) == 0) {
					outgoing.writeByte(21);
				}
				outgoing.writeSizeByte(outgoing.position - start);
			}
		}
	}

	public final void method33(int id) {
		int parameter = VariableParameter.parameters[id].parameter;
		if (parameter == 0) {
			return;
		}
		int state = settings[id];
		if (parameter == 1) {
			if (state == 1) {
				Rasterizer.generatePalette(0.9);
			}
			if (state == 2) {
				Rasterizer.generatePalette(0.8);
			}
			if (state == 3) {
				Rasterizer.generatePalette(0.7);
			}
			if (state == 4) {
				Rasterizer.generatePalette(0.6);
			}
			ItemDefinition.sprites.unlink();
			redrawBorders = true;
		}
		if (parameter == 3) {
			boolean previousPlayingMusic = playingMusic;
			if (state == 0) {
				adjustMidiVolume(playingMusic, 0);
				playingMusic = true;
			} else if (state == 1) {
				adjustMidiVolume(playingMusic, -400);
				playingMusic = true;
			} else if (state == 2) {
				adjustMidiVolume(playingMusic, -800);
				playingMusic = true;
			} else if (state == 3) {
				adjustMidiVolume(playingMusic, -1200);
				playingMusic = true;
			} else if (state == 4) {
				playingMusic = false;
			}

			if (playingMusic != previousPlayingMusic && !lowMemory) {
				if (playingMusic) {
					musicId = nextMusicId;
					fadeMusic = true;
					provider.provide(2, musicId);
				} else {
					stopMidi();
				}
				songDelay = 0;
			}
		} else if (parameter == 4) {
			if (state == 0) {
				aBoolean848 = true;
				setWaveVolume(0);
			} else if (state == 1) {
				aBoolean848 = true;
				setWaveVolume(-400);
			} else if (state == 2) {
				aBoolean848 = true;
				setWaveVolume(-800);
			} else if (state == 3) {
				aBoolean848 = true;
				setWaveVolume(-1200);
			} else if (state == 4) {
				aBoolean848 = false;
			}
		} else if (parameter == 5) {
			mouseButtons = state;
		} else if (parameter == 6) {
			chatEffects = state;
		}
		if (parameter == 8) {
			splitPrivateChat = state;
			redrawChatbox = true;
		}
		if (parameter == 9) {
			bankInsertMode = state;
		}
	}

	public final void method36() {
		aClass15_1163.initializeRasterizer();
		Rasterizer.scanOffsets = anIntArray1181;
		inventoryBackground.draw(0, 0);
		if (inventoryOverlayInterfaceId != -1) {
			method105(0, 0, Widget.widgets[inventoryOverlayInterfaceId], 0);
		} else if (inventoryTabIds[tabId] != -1) {
			method105(0, 0, Widget.widgets[inventoryTabIds[tabId]], 0);
		}
		if (menuOpen && anInt948 == 1) {
			method40();
		}
		aClass15_1163.drawImage(super.graphics, 553, 205);
		aClass15_1165.initializeRasterizer();
		Rasterizer.scanOffsets = anIntArray1182;
	}

	public final void method37(int j) {
		if (!lowMemory) {
			if (Rasterizer.anIntArray1480[17] >= j) {
				IndexedImage image = Rasterizer.floorImages[17];
				int pixels = image.width * image.height - 1;
				int j1 = image.width * tickDelta * 2;
				byte[] raster = image.raster;
				byte abyte3[] = aByteArray912;
				for (int i2 = 0; i2 <= pixels; i2++) {
					abyte3[i2] = raster[i2 - j1 & pixels];
				}

				image.raster = abyte3;
				aByteArray912 = raster;
				Rasterizer.method370(17);
				anInt854++;
				if (anInt854 > 1235) {
					anInt854 = 0;
					outgoing.writeOpcode(226);
					outgoing.writeByte(0);
					int l2 = outgoing.position;
					outgoing.writeShort(58722);
					outgoing.writeByte(240);
					outgoing.writeShort((int) (Math.random() * 65536D));
					outgoing.writeByte((int) (Math.random() * 256D));
					if ((int) (Math.random() * 2D) == 0) {
						outgoing.writeShort(51825);
					}
					outgoing.writeByte((int) (Math.random() * 256D));
					outgoing.writeShort((int) (Math.random() * 65536D));
					outgoing.writeShort(7130);
					outgoing.writeShort((int) (Math.random() * 65536D));
					outgoing.writeShort(61657);
					outgoing.writeSizeByte(outgoing.position - l2);
				}
			}
			if (Rasterizer.anIntArray1480[24] >= j) {
				IndexedImage image = Rasterizer.floorImages[24];
				int length = image.width * image.height - 1;
				int k1 = image.width * tickDelta * 2;
				byte[] raster = image.raster;
				byte[] abyte4 = aByteArray912;
				for (int j2 = 0; j2 <= length; j2++) {
					abyte4[j2] = raster[j2 - k1 & length];
				}

				image.raster = abyte4;
				aByteArray912 = raster;
				Rasterizer.method370(24);
			}
			if (Rasterizer.anIntArray1480[34] >= j) {
				IndexedImage image = Rasterizer.floorImages[34];
				int i1 = image.width * image.height - 1;
				int l1 = image.width * tickDelta * 2;
				byte abyte2[] = image.raster;
				byte abyte5[] = aByteArray912;
				for (int k2 = 0; k2 <= i1; k2++) {
					abyte5[k2] = abyte2[k2 - l1 & i1];
				}

				image.raster = abyte5;
				aByteArray912 = abyte2;
				Rasterizer.method370(34);
			}
		}
	}

	public final void method39() {
		int i = anInt1098 * 128 + 64;
		int j = anInt1099 * 128 + 64;
		int k = method42(i, j, plane) - anInt1100;
		if (anInt858 < i) {
			anInt858 += anInt1101 + (i - anInt858) * anInt1102 / 1000;
			if (anInt858 > i) {
				anInt858 = i;
			}
		}
		if (anInt858 > i) {
			anInt858 -= anInt1101 + (anInt858 - i) * anInt1102 / 1000;
			if (anInt858 < i) {
				anInt858 = i;
			}
		}
		if (anInt859 < k) {
			anInt859 += anInt1101 + (k - anInt859) * anInt1102 / 1000;
			if (anInt859 > k) {
				anInt859 = k;
			}
		}
		if (anInt859 > k) {
			anInt859 -= anInt1101 + (anInt859 - k) * anInt1102 / 1000;
			if (anInt859 < k) {
				anInt859 = k;
			}
		}
		if (anInt860 < j) {
			anInt860 += anInt1101 + (j - anInt860) * anInt1102 / 1000;
			if (anInt860 > j) {
				anInt860 = j;
			}
		}
		if (anInt860 > j) {
			anInt860 -= anInt1101 + (anInt860 - j) * anInt1102 / 1000;
			if (anInt860 < j) {
				anInt860 = j;
			}
		}
		i = anInt995 * 128 + 64;
		j = anInt996 * 128 + 64;
		k = method42(i, j, plane) - anInt997;
		int l = i - anInt858;
		int i1 = k - anInt859;
		int j1 = j - anInt860;
		int k1 = (int) Math.sqrt(l * l + j1 * j1);
		int l1 = (int) (Math.atan2(i1, k1) * 325.949D) & 0x7ff;
		int i2 = (int) (Math.atan2(l, j1) * -325.949D) & 0x7ff;
		if (l1 < 128) {
			l1 = 128;
		}
		if (l1 > 383) {
			l1 = 383;
		}
		if (anInt861 < l1) {
			anInt861 += anInt998 + (l1 - anInt861) * anInt999 / 1000;
			if (anInt861 > l1) {
				anInt861 = l1;
			}
		}
		if (anInt861 > l1) {
			anInt861 -= anInt998 + (anInt861 - l1) * anInt999 / 1000;
			if (anInt861 < l1) {
				anInt861 = l1;
			}
		}
		int j2 = i2 - anInt862;
		if (j2 > 1024) {
			j2 -= 2048;
		}
		if (j2 < -1024) {
			j2 += 2048;
		}
		if (j2 > 0) {
			anInt862 += anInt998 + j2 * anInt999 / 1000;
			anInt862 &= 0x7ff;
		}
		if (j2 < 0) {
			anInt862 -= anInt998 + -j2 * anInt999 / 1000;
			anInt862 &= 0x7ff;
		}
		int k2 = i2 - anInt862;
		if (k2 > 1024) {
			k2 -= 2048;
		}
		if (k2 < -1024) {
			k2 += 2048;
		}
		if (k2 < 0 && j2 > 0 || k2 > 0 && j2 < 0) {
			anInt862 = i2;
		}
	}

	public final void method40() {
		int x = anInt949;
		int y = anInt950;
		int width = anInt951;
		int height = anInt952;
		int colour = 0x5d5447;
		Raster.fillRectangle(x, y, height, width, colour);
		Raster.fillRectangle(x + 1, y + 1, 16, width - 2, 0);
		Raster.drawRectangle(x + 1, y + 18, width - 2, height - 19, 0);
		boldFont.render(x + 3, y + 14, "Choose Option", colour);
		int lastX = super.mouseEventX;
		int lastY = super.mouseEventY;

		if (anInt948 == 0) {
			lastX -= 4;
			lastY -= 4;
		} else if (anInt948 == 1) {
			lastX -= 553;
			lastY -= 205;
		} else if (anInt948 == 2) {
			lastX -= 17;
			lastY -= 357;
		}

		for (int i = 0; i < menuActionRow; i++) {
			int textY = y + 31 + (menuActionRow - 1 - i) * 15;
			int textColour = 0xffffff;
			if (lastX > x && lastX < x + width && lastY > textY - 13 && lastY < textY + 3) {
				textColour = 0xffff00;
			}
			boldFont.shadow(x + 3, textY, menuActionTexts[i], true, textColour);
		}
	}

	public final int method42(int x, int y, int z) {
		// Something to do with the tile height.
		int viewportX = x >> 7;
		int viewportY = y >> 7;
		if (viewportX < 0 || viewportY < 0 || viewportX > 103 || viewportY > 103) {
			return 0;
		}
		int plane = z;
		if (plane < 3 && (aByteArrayArrayArray1258[1][viewportX][viewportY] & 2) == 2) {
			plane++;
		}
		int sizeX = x & 0x7f;
		int sizeY = y & 0x7f;
		int i2 = anIntArrayArrayArray1214[plane][viewportX][viewportY] * (128 - sizeX)
				+ anIntArrayArrayArray1214[plane][viewportX + 1][viewportY] * sizeX >> 7;
		int j2 = anIntArrayArrayArray1214[plane][viewportX][viewportY + 1] * (128 - sizeX)
				+ anIntArrayArrayArray1214[plane][viewportX + 1][viewportY + 1] * sizeX >> 7;
		return i2 * (128 - sizeY) + j2 * sizeY >> 7;
	}

	public final boolean method48(Widget widget) {
		int contentType = widget.anInt214;
		if (friendServerStatus == 2) {
			if (contentType == 201) {
				redrawChatbox = true;
				inputDialogueState = 0;
				messagePromptRaised = true;
				messagePromptInput = "";
				friendListAction = 1;
				aString1121 = "Enter name of friend to add to list";
			}
			if (contentType == 202) {
				redrawChatbox = true;
				inputDialogueState = 0;
				messagePromptRaised = true;
				messagePromptInput = "";
				friendListAction = 2;
				aString1121 = "Enter name of friend to delete from list";
			}
		}
		if (contentType == 205) {
			anInt1011 = 250;
			return true;
		}
		if (contentType == 501) {
			redrawChatbox = true;
			inputDialogueState = 0;
			messagePromptRaised = true;
			messagePromptInput = "";
			friendListAction = 4;
			aString1121 = "Enter name of player to add to list";
		}
		if (contentType == 502) {
			redrawChatbox = true;
			inputDialogueState = 0;
			messagePromptRaised = true;
			messagePromptInput = "";
			friendListAction = 5;
			aString1121 = "Enter name of player to delete from list";
		}
		if (contentType >= 300 && contentType <= 313) {
			int part = (contentType - 300) / 2;
			int shift = contentType & 1;
			int style = characterDesignStyles[part];
			if (style != -1) {
				do {
					if (shift == 0 && --style < 0) {
						style = IdentityKit.count - 1;
					}
					if (shift == 1 && ++style >= IdentityKit.count) {
						style = 0;
					}
				} while (IdentityKit.kits[style].validStyle || IdentityKit.kits[style].part != part + (maleAvatar ? 0 : 7));
				characterDesignStyles[part] = style;
				avatarChanged = true;
			}
		}
		if (contentType >= 314 && contentType <= 323) {
			int part = (contentType - 314) / 2;
			int k1 = contentType & 1;
			int colour = characterDesignColours[part];
			if (k1 == 0 && --colour < 0) {
				colour = PLAYER_BODY_RECOLOURS[part].length - 1;
			}
			if (k1 == 1 && ++colour >= PLAYER_BODY_RECOLOURS[part].length) {
				colour = 0;
			}
			characterDesignColours[part] = colour;
			avatarChanged = true;
		}
		if (contentType == 324 && !maleAvatar) {
			maleAvatar = true;
			changeCharacterGender();
		}
		if (contentType == 325 && maleAvatar) {
			maleAvatar = false;
			changeCharacterGender();
		}
		if (contentType == 326) {
			outgoing.writeOpcode(101);
			outgoing.writeByte(maleAvatar ? 0 : 1);
			for (int index = 0; index < 7; index++) {
				outgoing.writeByte(characterDesignStyles[index]);
			}

			for (int index = 0; index < 5; index++) {
				outgoing.writeByte(characterDesignColours[index]);
			}

			return true;
		}
		if (contentType == 613) {
			reportAbuseMuteToggle = !reportAbuseMuteToggle;
		}
		if (contentType >= 601 && contentType <= 612) {
			clearTopInterfaces();
			if (reportInput.length() > 0) {
				outgoing.writeOpcode(218);
				outgoing.writeLong(StringUtils.encodeBase37(reportInput));
				outgoing.writeByte(contentType - 601);
				outgoing.writeByte(reportAbuseMuteToggle ? 1 : 0);
			}
		}
		return false;
	}

	public final void method50(int x, int y, int z, int nullColour, int defaultColour) {
		int key = scene.getWallKey(x, y, z);

		if (key != 0) {
			int l1 = scene.getConfig(x, y, z, key);
			int k2 = l1 >> 6 & 3;
			int i3 = l1 & 0x1f;

			int colour = nullColour;
			if (key > 0) {
				colour = defaultColour;
			}

			int raster[] = aClass30_Sub2_Sub1_Sub1_1263.raster;
			int k4 = 24624 + x * 4 + (103 - y) * 512 * 4;
			int id = key >> 14 & 0x7fff;
			ObjectDefinition definition = ObjectDefinition.lookup(id);

			if (definition.mapscene != -1) {
				IndexedImage image = mapScenes[definition.mapscene];
				if (image != null) {
					int i6 = (definition.width * 4 - image.width) / 2;
					int j6 = (definition.length * 4 - image.height) / 2;
					image.draw(48 + x * 4 + i6, 48 + (104 - y - definition.length) * 4 + j6);
				}
			} else {
				if (i3 == 0 || i3 == 2) {
					if (k2 == 0) {
						raster[k4] = colour;
						raster[k4 + 512] = colour;
						raster[k4 + 1024] = colour;
						raster[k4 + 1536] = colour;
					} else if (k2 == 1) {
						raster[k4] = colour;
						raster[k4 + 1] = colour;
						raster[k4 + 2] = colour;
						raster[k4 + 3] = colour;
					} else if (k2 == 2) {
						raster[k4 + 3] = colour;
						raster[k4 + 3 + 512] = colour;
						raster[k4 + 3 + 1024] = colour;
						raster[k4 + 3 + 1536] = colour;
					} else if (k2 == 3) {
						raster[k4 + 1536] = colour;
						raster[k4 + 1536 + 1] = colour;
						raster[k4 + 1536 + 2] = colour;
						raster[k4 + 1536 + 3] = colour;
					}
				}
				if (i3 == 3) {
					if (k2 == 0) {
						raster[k4] = colour;
					} else if (k2 == 1) {
						raster[k4 + 3] = colour;
					} else if (k2 == 2) {
						raster[k4 + 3 + 1536] = colour;
					} else if (k2 == 3) {
						raster[k4 + 1536] = colour;
					}
				}
				if (i3 == 2) {
					if (k2 == 3) {
						raster[k4] = colour;
						raster[k4 + 512] = colour;
						raster[k4 + 1024] = colour;
						raster[k4 + 1536] = colour;
					} else if (k2 == 0) {
						raster[k4] = colour;
						raster[k4 + 1] = colour;
						raster[k4 + 2] = colour;
						raster[k4 + 3] = colour;
					} else if (k2 == 1) {
						raster[k4 + 3] = colour;
						raster[k4 + 3 + 512] = colour;
						raster[k4 + 3 + 1024] = colour;
						raster[k4 + 3 + 1536] = colour;
					} else if (k2 == 2) {
						raster[k4 + 1536] = colour;
						raster[k4 + 1536 + 1] = colour;
						raster[k4 + 1536 + 2] = colour;
						raster[k4 + 1536 + 3] = colour;
					}
				}
			}
		}

		key = scene.getInteractableObjectKey(x, y, z);
		if (key != 0) {
			int i2 = scene.getConfig(x, y, z, key);
			int l2 = i2 >> 6 & 3;
			int j3 = i2 & 0x1f;
			int id = key >> 14 & 0x7fff;
			ObjectDefinition definition = ObjectDefinition.lookup(id);

			if (definition.mapscene != -1) {
				IndexedImage image = mapScenes[definition.mapscene];
				if (image != null) {
					int j5 = (definition.width * 4 - image.width) / 2;
					int k5 = (definition.length * 4 - image.height) / 2;
					image.draw(48 + x * 4 + j5, 48 + (104 - y - definition.length) * 4 + k5);
				}
			} else if (j3 == 9) {
				int colour = 0xeeeeee;
				if (key > 0) {
					colour = 0xee0000;
				}
				int raster[] = aClass30_Sub2_Sub1_Sub1_1263.raster;
				int l5 = 24624 + x * 4 + (103 - y) * 512 * 4;
				if (l2 == 0 || l2 == 2) {
					raster[l5 + 1536] = colour;
					raster[l5 + 1024 + 1] = colour;
					raster[l5 + 512 + 2] = colour;
					raster[l5 + 3] = colour;
				} else {
					raster[l5] = colour;
					raster[l5 + 512 + 1] = colour;
					raster[l5 + 1024 + 2] = colour;
					raster[l5 + 1536 + 3] = colour;
				}
			}
		}

		key = scene.getFloorDecorationKey(x, y, z);
		if (key != 0) {
			int id = key >> 14 & 0x7fff;
			ObjectDefinition definition = ObjectDefinition.lookup(id);
			if (definition.mapscene != -1) {
				IndexedImage image = mapScenes[definition.mapscene];
				if (image != null) {
					int i4 = (definition.width * 4 - image.width) / 2;
					int j4 = (definition.length * 4 - image.height) / 2;
					image.draw(48 + x * 4 + i4, 48 + (104 - y - definition.length) * 4 + j4);
				}
			}
		}
	}

	public final void method51() {
		titleBox = new IndexedImage(titleScreen, "titlebox", 0);
		titleButton = new IndexedImage(titleScreen, "titlebutton", 0);
		runes = new IndexedImage[12];
		int icon = 0;
		try {
			icon = Integer.parseInt(getParameter("fl_icon"));
		} catch (Exception _ex) {
		}
		if (icon == 0) {
			for (int k = 0; k < 12; k++) {
				runes[k] = new IndexedImage(titleScreen, "runes", k);
			}

		} else {
			for (int l = 0; l < 12; l++) {
				runes[l] = new IndexedImage(titleScreen, "runes", 12 + (l & 3));
			}

		}
		aClass30_Sub2_Sub1_Sub1_1201 = new DirectSprite(128, 265);
		aClass30_Sub2_Sub1_Sub1_1202 = new DirectSprite(128, 265);
		for (int i1 = 0; i1 < 33920; i1++) {
			aClass30_Sub2_Sub1_Sub1_1201.raster[i1] = aClass15_1110.pixels[i1];
		}

		for (int j1 = 0; j1 < 33920; j1++) {
			aClass30_Sub2_Sub1_Sub1_1202.raster[j1] = aClass15_1111.pixels[j1];
		}

		anIntArray851 = new int[256];
		for (int k1 = 0; k1 < 64; k1++) {
			anIntArray851[k1] = k1 * 0x40000;
		}

		for (int l1 = 0; l1 < 64; l1++) {
			anIntArray851[l1 + 64] = 0xff0000 + 1024 * l1;
		}

		for (int i2 = 0; i2 < 64; i2++) {
			anIntArray851[i2 + 128] = 0xffff00 + 4 * i2;
		}

		for (int j2 = 0; j2 < 64; j2++) {
			anIntArray851[j2 + 192] = 0xffffff;
		}

		anIntArray852 = new int[256];
		for (int k2 = 0; k2 < 64; k2++) {
			anIntArray852[k2] = k2 * 1024;
		}

		for (int l2 = 0; l2 < 64; l2++) {
			anIntArray852[l2 + 64] = 65280 + 4 * l2;
		}

		for (int i3 = 0; i3 < 64; i3++) {
			anIntArray852[i3 + 128] = 65535 + 0x40000 * i3;
		}

		for (int j3 = 0; j3 < 64; j3++) {
			anIntArray852[j3 + 192] = 0xffffff;
		}

		anIntArray853 = new int[256];
		for (int k3 = 0; k3 < 64; k3++) {
			anIntArray853[k3] = k3 * 4;
		}

		for (int l3 = 0; l3 < 64; l3++) {
			anIntArray853[l3 + 64] = 255 + 0x40000 * l3;
		}

		for (int i4 = 0; i4 < 64; i4++) {
			anIntArray853[i4 + 128] = 0xff00ff + 1024 * i4;
		}

		for (int j4 = 0; j4 < 64; j4++) {
			anIntArray853[j4 + 192] = 0xffffff;
		}

		anIntArray850 = new int[256];
		anIntArray1190 = new int[32768];
		anIntArray1191 = new int[32768];
		method106(null);
		anIntArray828 = new int[32768];
		anIntArray829 = new int[32768];
		drawLoadingText(10, "Connecting to fileserver");

		if (!aBoolean831) {
			aBoolean880 = true;
			aBoolean831 = true;
			startRunnable(this, 2);
		}
	}

	public final void method53() {
		if (lowMemory && loadingStage == 2 && Region.anInt131 != plane) {
			aClass15_1165.initializeRasterizer();
			frameFont.renderCentre(257, 151, "Loading - please wait.", 0);
			frameFont.renderCentre(256, 150, "Loading - please wait.", 0xFFFFFF);
			aClass15_1165.drawGameImage(super.graphics, 4, 4);
			loadingStage = 1;
			loadingStartTime = System.currentTimeMillis();
		}

		if (loadingStage == 1) {
			int j = method54();
			if (j != 0 && System.currentTimeMillis() - loadingStartTime > 0x57e40) {
				SignLink.reportError(username + " glcfb " + serverSeed + "," + j + "," + lowMemory + "," + indices[0] + ","
						+ provider.remaining() + "," + plane + "," + sectorX + "," + sectorY);
				loadingStartTime = System.currentTimeMillis();
			}
		}

		if (loadingStage == 2 && plane != anInt985) {
			anInt985 = plane;
			method24(plane);
		}
	}

	public final int method54() {
		for (int i = 0; i < localRegionMapData.length; i++) {
			if (localRegionMapData[i] == null && localRegionMapIds[i] != -1) {
				return -1;
			} else if (localRegionLandscapeData[i] == null && localRegionLandscapeIds[i] != -1) {
				return -2;
			}
		}

		boolean ready = true;
		for (int i = 0; i < localRegionMapData.length; i++) {
			byte[] data = localRegionLandscapeData[i];
			if (data != null) {
				int x = (localRegionIds[i] >> 8) * 64 - regionBaseX;
				int y = (localRegionIds[i] & 0xff) * 64 - regionBaseY;

				if (constructedViewport) {
					x = y = 10;
				}
				ready &= Region.objectsReady(data, x, y);
			}
		}

		if (!ready) {
			return -3;
		} else if (validLocalMap) {
			return -4;
		}

		loadingStage = 2;
		Region.anInt131 = plane;
		method22();
		outgoing.writeOpcode(121);
		return 0;
	}

	public final void method56() {
		DirectSprite sprite = new DirectSprite(titleScreen.extract("title.dat"), this);
		aClass15_1110.initializeRasterizer();
		sprite.method346(0, 0);
		aClass15_1111.initializeRasterizer();
		sprite.method346(-637, 0);
		aClass15_1107.initializeRasterizer();
		sprite.method346(-128, 0);
		aClass15_1108.initializeRasterizer();
		sprite.method346(-202, -371);
		aClass15_1109.initializeRasterizer();
		sprite.method346(-202, -171);
		aClass15_1112.initializeRasterizer();
		sprite.method346(0, -265);
		aClass15_1113.initializeRasterizer();
		sprite.method346(-562, -265);
		aClass15_1114.initializeRasterizer();
		sprite.method346(-128, -171);
		aClass15_1115.initializeRasterizer();
		sprite.method346(-562, -171);
		int raster[] = new int[sprite.width];
		for (int y = 0; y < sprite.height; y++) {
			for (int x = 0; x < sprite.width; x++) {
				raster[x] = sprite.raster[sprite.width - x - 1 + sprite.width * y];
			}

			for (int x = 0; x < sprite.width; x++) {
				sprite.raster[x + sprite.width * y] = raster[x];
			}
		}

		aClass15_1110.initializeRasterizer();
		sprite.method346(382, 0);
		aClass15_1111.initializeRasterizer();
		sprite.method346(-255, 0);
		aClass15_1107.initializeRasterizer();
		sprite.method346(254, 0);
		aClass15_1108.initializeRasterizer();
		sprite.method346(180, -371);
		aClass15_1109.initializeRasterizer();
		sprite.method346(180, -171);
		aClass15_1112.initializeRasterizer();
		sprite.method346(382, -265);
		aClass15_1113.initializeRasterizer();
		sprite.method346(-180, -265);
		aClass15_1114.initializeRasterizer();
		sprite.method346(254, -171);
		aClass15_1115.initializeRasterizer();
		sprite.method346(-180, -171);
		sprite = new DirectSprite(titleScreen, "logo", 0);
		aClass15_1107.initializeRasterizer();
		sprite.drawSprite(382 - sprite.width / 2 - 128, 18);
		sprite = null;
		System.gc();
	}

	public final void method58() {
		char c = 256;
		for (int j = 10; j < 117; j++) {
			int k = (int) (Math.random() * 100);
			if (k < 50) {
				anIntArray828[j + (c - 2 << 7)] = 255;
			}
		}

		for (int l = 0; l < 100; l++) {
			int i1 = (int) (Math.random() * 124D) + 2;
			int k1 = (int) (Math.random() * 128D) + 128;
			int k2 = i1 + (k1 << 7);
			anIntArray828[k2] = 192;
		}

		for (int j1 = 1; j1 < c - 1; j1++) {
			for (int l1 = 1; l1 < 127; l1++) {
				int l2 = l1 + (j1 << 7);
				anIntArray829[l2] = (anIntArray828[l2 - 1] + anIntArray828[l2 + 1] + anIntArray828[l2 - 128] + anIntArray828[l2 + 128]) / 4;
			}

		}

		anInt1275 += 128;
		if (anInt1275 > anIntArray1190.length) {
			anInt1275 -= anIntArray1190.length;
			int i2 = (int) (Math.random() * 12D);
			method106(runes[i2]);
		}
		for (int j2 = 1; j2 < c - 1; j2++) {
			for (int i3 = 1; i3 < 127; i3++) {
				int k3 = i3 + (j2 << 7);
				int i4 = anIntArray829[k3 + 128] - anIntArray1190[k3 + anInt1275 & anIntArray1190.length - 1] / 5;
				if (i4 < 0) {
					i4 = 0;
				}
				anIntArray828[k3] = i4;
			}

		}

		for (int j3 = 0; j3 < c - 1; j3++) {
			anIntArray969[j3] = anIntArray969[j3 + 1];
		}

		anIntArray969[c - 1] = (int) (Math.sin(tick / 14D) * 16D + Math.sin(tick / 15D) * 14D + Math.sin(tick / 16D) * 12D);
		if (anInt1040 > 0) {
			anInt1040 -= 4;
		}
		if (anInt1041 > 0) {
			anInt1041 -= 4;
		}
		if (anInt1040 == 0 && anInt1041 == 0) {
			int l3 = (int) (Math.random() * 2000D);
			if (l3 == 0) {
				anInt1040 = 1024;
			}
			if (l3 == 1) {
				anInt1041 = 1024;
			}
		}
	}

	public final void method61() {
		if (headIconDrawType != 2) {
			return;
		}
		method128((anInt934 - regionBaseX << 7) + anInt937, anInt936 * 2, (anInt935 - regionBaseY << 7) + anInt938);
		if (spriteDrawX > -1 && tick % 20 < 10) {
			headIcons[2].drawSprite(spriteDrawX - 12, spriteDrawY - 28);
		}
	}

	public final void method64() {
		if (aClass15_1107 != null) {
			return;
		}
		super.frameGraphicsBuffer = null;
		aClass15_1166 = null;
		aClass15_1164 = null;
		aClass15_1163 = null;
		aClass15_1165 = null;
		aClass15_1123 = null;
		aClass15_1124 = null;
		aClass15_1125 = null;
		aClass15_1110 = new ProducingGraphicsBuffer(getFrame(), 128, 265);
		Raster.reset();
		aClass15_1111 = new ProducingGraphicsBuffer(getFrame(), 128, 265);
		Raster.reset();
		aClass15_1107 = new ProducingGraphicsBuffer(getFrame(), 509, 171);
		Raster.reset();
		aClass15_1108 = new ProducingGraphicsBuffer(getFrame(), 360, 132);
		Raster.reset();
		aClass15_1109 = new ProducingGraphicsBuffer(getFrame(), 360, 200);
		Raster.reset();
		aClass15_1112 = new ProducingGraphicsBuffer(getFrame(), 202, 238);
		Raster.reset();
		aClass15_1113 = new ProducingGraphicsBuffer(getFrame(), 203, 238);
		Raster.reset();
		aClass15_1114 = new ProducingGraphicsBuffer(getFrame(), 74, 94);
		Raster.reset();
		aClass15_1115 = new ProducingGraphicsBuffer(getFrame(), 75, 94);
		Raster.reset();
		if (titleScreen != null) {
			method56();
			method51();
		}
		redrawBorders = true;
	}

	public final void method65(int i, int j, int k, int l, Widget class9, int i1, boolean flag, int j1, int k1) {
		if (aBoolean972) {
			anInt992 = 32;
		} else {
			anInt992 = 0;
		}
		aBoolean972 = false;
		packetSize += k1;
		if (k >= i && k < i + 16 && l >= i1 && l < i1 + 16) {
			class9.scrollPosition -= anInt1213 * 4;
			if (flag) {
				redrawTabArea = true;
				return;
			}
		} else if (k >= i && k < i + 16 && l >= i1 + j - 16 && l < i1 + j) {
			class9.scrollPosition += anInt1213 * 4;
			if (flag) {
				redrawTabArea = true;
				return;
			}
		} else if (k >= i - anInt992 && k < i + 16 + anInt992 && l >= i1 + 16 && l < i1 + j - 16 && anInt1213 > 0) {
			int l1 = (j - 32) * j / j1;
			if (l1 < 8) {
				l1 = 8;
			}
			int i2 = l - i1 - 16 - l1 / 2;
			int j2 = j - 32 - l1;
			class9.scrollPosition = (j1 - j) * i2 / j2;
			if (flag) {
				redrawTabArea = true;
			}
			aBoolean972 = true;
		}
	}

	public final boolean method66(int clickedItem, int y, int x) {
		int id = clickedItem >> 14 & 0x7fff;
		int config = scene.getConfig(x, y, plane, clickedItem);
		if (config == -1) {
			return false;
		}
		int type = config & 0x1f;
		int orientation = config >> 6 & 3;
		if (type == 10 || type == 11 || type == 22) {
			ObjectDefinition definition = ObjectDefinition.lookup(id);
			int width;
			int height;
			if (orientation == 0 || orientation == 2) {
				width = definition.width;
				height = definition.length;
			} else {
				width = definition.length;
				height = definition.width;
			}
			int surroundings = definition.surroundings;
			if (orientation != 0) {
				surroundings = (surroundings << orientation & 0xf) + (surroundings >> 4 - orientation);
			}
			method85(2, 0, height, 0, localPlayer.pathY[0], width, surroundings, y, localPlayer.pathX[0], false, x);
		} else {
			method85(2, orientation, 0, type + 1, localPlayer.pathY[0], 0, 0, y, localPlayer.pathX[0], false, x);
		}
		anInt914 = super.lastClickX;
		anInt915 = super.lastClickY;
		anInt917 = 2;
		anInt916 = 0;
		return true;
	}

	public final void method69(int row) {
		if (row < 0) {
			return;
		} else if (inputDialogueState != 0) {
			inputDialogueState = 0;
			redrawChatbox = true;
		}

		int x = anIntArray1091[row];
		int id = anIntArray1092[row];
		int action = menuActionIds[row];
		int clicked = anIntArray1094[row];

		if (action >= 2000) {
			action -= 2000;
		}

		if (action == 582) {
			Npc npc = npcs[clicked];
			if (npc != null) {
				method85(2, 0, 1, 0, localPlayer.pathY[0], 1, 0, npc.pathY[0], localPlayer.pathX[0], false, npc.pathX[0]);
				anInt914 = super.lastClickX;
				anInt915 = super.lastClickY;
				anInt917 = 2;
				anInt916 = 0;
				outgoing.writeOpcode(57); // item on npc
				outgoing.writeShortA(anInt1285); // id
				outgoing.writeShortA(clicked); // npc index
				outgoing.writeLEShort(anInt1283); // slot
				outgoing.writeShortA(anInt1284); // container widget id
			}
		} else if (action == 234) {
			boolean flag1 = method85(2, 0, 0, 0, localPlayer.pathY[0], 0, 0, id, localPlayer.pathX[0], false, x);
			if (!flag1) {
				flag1 = method85(2, 0, 1, 0, localPlayer.pathY[0], 1, 0, id, localPlayer.pathX[0], false, x);
			}
			anInt914 = super.lastClickX;
			anInt915 = super.lastClickY;
			anInt917 = 2;
			anInt916 = 0;
			outgoing.writeOpcode(236);
			outgoing.writeLEShort(id + regionBaseY);
			outgoing.writeShort(clicked);
			outgoing.writeLEShort(x + regionBaseX);
		}
		if (action == 62 && method66(clicked, id, x)) {
			outgoing.writeOpcode(192);
			outgoing.writeShort(anInt1284);
			outgoing.writeLEShort(clicked >> 14 & 0x7fff); // object
			outgoing.writeLEShortA(id + regionBaseY); // y
			outgoing.writeLEShort(anInt1283); // slot
			outgoing.writeLEShortA(x + regionBaseX); // x
			outgoing.writeShort(anInt1285); // id
		}
		if (action == 511) {
			boolean flag2 = method85(2, 0, 0, 0, localPlayer.pathY[0], 0, 0, id, localPlayer.pathX[0], false, x);
			if (!flag2) {
				flag2 = method85(2, 0, 1, 0, localPlayer.pathY[0], 1, 0, id, localPlayer.pathX[0], false, x);
			}
			anInt914 = super.lastClickX;
			anInt915 = super.lastClickY;
			anInt917 = 2;
			anInt916 = 0;
			outgoing.writeOpcode(25);
			outgoing.writeLEShort(anInt1284);
			outgoing.writeShortA(anInt1285);
			outgoing.writeShort(clicked);
			outgoing.writeShortA(id + regionBaseY);
			outgoing.writeLEShortA(anInt1283);
			outgoing.writeShort(x + regionBaseX);
		}
		if (action == 74) {
			outgoing.writeOpcode(122);
			outgoing.writeLEShortA(id);
			outgoing.writeShortA(x);
			outgoing.writeLEShort(clicked);
			anInt1243 = 0;
			anInt1244 = id;
			anInt1245 = x;
			anInt1246 = 2;
			if (Widget.widgets[id].parent == openInterfaceId) {
				anInt1246 = 1;
			}
			if (Widget.widgets[id].parent == backDialogueId) {
				anInt1246 = 3;
			}
		}
		if (action == 315) {
			Widget class9 = Widget.widgets[id];
			boolean flag8 = true;
			if (class9.anInt214 > 0) {
				flag8 = method48(class9);
			}
			if (flag8) {
				outgoing.writeOpcode(185);
				outgoing.writeShort(id);
			}
		}
		if (action == 561) {
			Player player = players[clicked];
			if (player != null) {
				method85(2, 0, 1, 0, localPlayer.pathY[0], 1, 0, player.pathY[0], localPlayer.pathX[0], false, player.pathX[0]);
				anInt914 = super.lastClickX;
				anInt915 = super.lastClickY;
				anInt917 = 2;
				anInt916 = 0;
				anInt1188 += clicked;
				if (anInt1188 >= 90) {
					outgoing.writeOpcode(136);
					anInt1188 = 0;
				}
				outgoing.writeOpcode(128);
				outgoing.writeShort(clicked);
			}
		}
		if (action == 20) {
			Npc npc = npcs[clicked];
			if (npc != null) {
				method85(2, 0, 1, 0, localPlayer.pathY[0], 1, 0, npc.pathY[0], localPlayer.pathX[0], false, npc.pathX[0]);
				anInt914 = super.lastClickX;
				anInt915 = super.lastClickY;
				anInt917 = 2;
				anInt916 = 0;
				outgoing.writeOpcode(155);
				outgoing.writeLEShort(clicked);
			}
		}
		if (action == 779) {
			Player player = players[clicked];
			if (player != null) {
				method85(2, 0, 1, 0, localPlayer.pathY[0], 1, 0, player.pathY[0], localPlayer.pathX[0], false, player.pathX[0]);
				anInt914 = super.lastClickX;
				anInt915 = super.lastClickY;
				anInt917 = 2;
				anInt916 = 0;
				outgoing.writeOpcode(153);
				outgoing.writeLEShort(clicked);
			}
		}
		if (action == 516) {
			if (!menuOpen) {
				scene.method312(super.lastClickY - 4, super.lastClickX - 4);
			} else {
				scene.method312(id - 4, x - 4);
			}
		}
		if (action == 1062) {
			anInt924 += regionBaseX;
			if (anInt924 >= 113) {
				outgoing.writeOpcode(183);
				outgoing.writeTriByte(0xe63271);
				anInt924 = 0;
			}
			method66(clicked, id, x);
			outgoing.writeOpcode(228);
			outgoing.writeShortA(clicked >> 14 & 0x7fff);
			outgoing.writeShortA(id + regionBaseY);
			outgoing.writeShort(x + regionBaseX);
		}
		if (action == 679 && !aBoolean1149) {
			outgoing.writeOpcode(40);
			outgoing.writeShort(id);
			aBoolean1149 = true;
		}
		if (action == 431) {
			outgoing.writeOpcode(129);
			outgoing.writeShortA(x);
			outgoing.writeShort(id);
			outgoing.writeShortA(clicked);
			anInt1243 = 0;
			anInt1244 = id;
			anInt1245 = x;
			anInt1246 = 2;
			if (Widget.widgets[id].parent == openInterfaceId) {
				anInt1246 = 1;
			}
			if (Widget.widgets[id].parent == backDialogueId) {
				anInt1246 = 3;
			}
		}
		if (action == 337 || action == 42 || action == 792 || action == 322) {
			String s = menuActionTexts[row];
			int k1 = s.indexOf("@whi@");
			if (k1 != -1) {
				long nameHash = StringUtils.encodeBase37(s.substring(k1 + 5).trim());
				if (action == 337) {
					addFriend(nameHash);
				}
				if (action == 42) {
					addIgnore(nameHash);
				}
				if (action == 792) {
					removeFriend(nameHash);
				}
				if (action == 322) {
					removeIgnore(nameHash);
				}
			}
		}
		if (action == 53) {
			outgoing.writeOpcode(135);
			outgoing.writeLEShort(x);
			outgoing.writeShortA(id);
			outgoing.writeLEShort(clicked);
			anInt1243 = 0;
			anInt1244 = id;
			anInt1245 = x;
			anInt1246 = 2;
			if (Widget.widgets[id].parent == openInterfaceId) {
				anInt1246 = 1;
			}
			if (Widget.widgets[id].parent == backDialogueId) {
				anInt1246 = 3;
			}
		}
		if (action == 539) {
			outgoing.writeOpcode(16);
			outgoing.writeShortA(clicked);
			outgoing.writeLEShortA(x);
			outgoing.writeLEShortA(id);
			anInt1243 = 0;
			anInt1244 = id;
			anInt1245 = x;
			anInt1246 = 2;
			if (Widget.widgets[id].parent == openInterfaceId) {
				anInt1246 = 1;
			}
			if (Widget.widgets[id].parent == backDialogueId) {
				anInt1246 = 3;
			}
		}
		if (action == 484 || action == 6) {
			String s1 = menuActionTexts[row];
			int l1 = s1.indexOf("@whi@");
			if (l1 != -1) {
				s1 = s1.substring(l1 + 5).trim();
				String s7 = StringUtils.format(StringUtils.decodeBase37(StringUtils.encodeBase37(s1)));
				boolean flag9 = false;
				for (int j3 = 0; j3 < playerCount; j3++) {
					Player player = players[playerList[j3]];
					if (player == null || player.name == null || !player.name.equalsIgnoreCase(s7)) {
						continue;
					}
					method85(2, 0, 1, 0, localPlayer.pathY[0], 1, 0, player.pathY[0], localPlayer.pathX[0], false,
							player.pathX[0]);
					if (action == 484) {
						outgoing.writeOpcode(139);
						outgoing.writeLEShort(playerList[j3]);
					}
					if (action == 6) {
						anInt1188 += clicked;
						if (anInt1188 >= 90) {
							outgoing.writeOpcode(136);
							anInt1188 = 0;
						}
						outgoing.writeOpcode(128);
						outgoing.writeShort(playerList[j3]);
					}
					flag9 = true;
					break;
				}

				if (!flag9) {
					addChatMessage(0, "Unable to find " + s7, "");
				}
			}
		}
		if (action == 870) {
			outgoing.writeOpcode(53); // item on item
			outgoing.writeShort(x); // target slot
			outgoing.writeShortA(anInt1283); // current slot
			outgoing.writeLEShortA(clicked); // target id
			outgoing.writeShort(anInt1284); // target interface
			outgoing.writeLEShort(anInt1285); // used id
			outgoing.writeShort(id); // used interface
			anInt1243 = 0;
			anInt1244 = id;
			anInt1245 = x;
			anInt1246 = 2;
			if (Widget.widgets[id].parent == openInterfaceId) {
				anInt1246 = 1;
			}
			if (Widget.widgets[id].parent == backDialogueId) {
				anInt1246 = 3;
			}
		}
		if (action == 847) {
			outgoing.writeOpcode(87);
			outgoing.writeShortA(clicked);
			outgoing.writeShort(id);
			outgoing.writeShortA(x);
			anInt1243 = 0;
			anInt1244 = id;
			anInt1245 = x;
			anInt1246 = 2;
			if (Widget.widgets[id].parent == openInterfaceId) {
				anInt1246 = 1;
			}
			if (Widget.widgets[id].parent == backDialogueId) {
				anInt1246 = 3;
			}
		}
		if (action == 626) {
			Widget widget = Widget.widgets[id];
			selectedSpellId = 1;
			anInt1137 = id;
			anInt1138 = widget.anInt237;
			selectedItemId = 0;
			redrawTabArea = true;
			String s4 = widget.aString222;
			if (s4.indexOf(" ") != -1) {
				s4 = s4.substring(0, s4.indexOf(" "));
			}
			String s8 = widget.aString222;
			if (s8.indexOf(" ") != -1) {
				s8 = s8.substring(s8.indexOf(" ") + 1);
			}
			selectedSpellName = s4 + " " + widget.aString218 + " " + s8;
			if (anInt1138 == 16) {
				redrawTabArea = true;
				tabId = 3;
				aBoolean1103 = true;
			}
			return;
		}
		if (action == 78) {
			outgoing.writeOpcode(117);
			outgoing.writeLEShortA(id);
			outgoing.writeLEShortA(clicked);
			outgoing.writeLEShort(x);
			anInt1243 = 0;
			anInt1244 = id;
			anInt1245 = x;
			anInt1246 = 2;
			if (Widget.widgets[id].parent == openInterfaceId) {
				anInt1246 = 1;
			}
			if (Widget.widgets[id].parent == backDialogueId) {
				anInt1246 = 3;
			}
		}
		if (action == 27) {
			Player player = players[clicked];
			if (player != null) {
				method85(2, 0, 1, 0, localPlayer.pathY[0], 1, 0, player.pathY[0], localPlayer.pathX[0], false, player.pathX[0]);
				anInt914 = super.lastClickX;
				anInt915 = super.lastClickY;
				anInt917 = 2;
				anInt916 = 0;
				anInt986 += clicked;
				if (anInt986 >= 54) {
					outgoing.writeOpcode(189);
					outgoing.writeByte(234);
					anInt986 = 0;
				}
				outgoing.writeOpcode(73);
				outgoing.writeLEShort(clicked);
			}
		}
		if (action == 213) {
			boolean flag3 = method85(2, 0, 0, 0, localPlayer.pathY[0], 0, 0, id, localPlayer.pathX[0], false, x);
			if (!flag3) {
				flag3 = method85(2, 0, 1, 0, localPlayer.pathY[0], 1, 0, id, localPlayer.pathX[0], false, x);
			}
			anInt914 = super.lastClickX;
			anInt915 = super.lastClickY;
			anInt917 = 2;
			anInt916 = 0;
			outgoing.writeOpcode(79);
			outgoing.writeLEShort(id + regionBaseY);
			outgoing.writeShort(clicked);
			outgoing.writeShortA(x + regionBaseX);
		}
		if (action == 632) {
			outgoing.writeOpcode(145);
			outgoing.writeShortA(id);
			outgoing.writeShortA(x);
			outgoing.writeShortA(clicked);
			anInt1243 = 0;
			anInt1244 = id;
			anInt1245 = x;
			anInt1246 = 2;
			if (Widget.widgets[id].parent == openInterfaceId) {
				anInt1246 = 1;
			}
			if (Widget.widgets[id].parent == backDialogueId) {
				anInt1246 = 3;
			}
		}
		if (action == 493) {
			outgoing.writeOpcode(75);
			outgoing.writeLEShortA(id);
			outgoing.writeLEShort(x);
			outgoing.writeShortA(clicked);
			anInt1243 = 0;
			anInt1244 = id;
			anInt1245 = x;
			anInt1246 = 2;
			if (Widget.widgets[id].parent == openInterfaceId) {
				anInt1246 = 1;
			}
			if (Widget.widgets[id].parent == backDialogueId) {
				anInt1246 = 3;
			}
		}
		if (action == 652) {
			boolean flag4 = method85(2, 0, 0, 0, localPlayer.pathY[0], 0, 0, id, localPlayer.pathX[0], false, x);
			if (!flag4) {
				flag4 = method85(2, 0, 1, 0, localPlayer.pathY[0], 1, 0, id, localPlayer.pathX[0], false, x);
			}
			anInt914 = super.lastClickX;
			anInt915 = super.lastClickY;
			anInt917 = 2;
			anInt916 = 0;
			outgoing.writeOpcode(156);
			outgoing.writeShortA(x + regionBaseX);
			outgoing.writeLEShort(id + regionBaseY);
			outgoing.writeLEShortA(clicked);
		}
		if (action == 94) {
			boolean flag5 = method85(2, 0, 0, 0, localPlayer.pathY[0], 0, 0, id, localPlayer.pathX[0], false, x);
			if (!flag5) {
				flag5 = method85(2, 0, 1, 0, localPlayer.pathY[0], 1, 0, id, localPlayer.pathX[0], false, x);
			}
			anInt914 = super.lastClickX;
			anInt915 = super.lastClickY;
			anInt917 = 2;
			anInt916 = 0;
			outgoing.writeOpcode(181);
			outgoing.writeLEShort(id + regionBaseY); // y
			outgoing.writeShort(clicked); // item id
			outgoing.writeLEShort(x + regionBaseX); // x
			outgoing.writeShortA(anInt1137); // spell id
		}
		if (action == 646) {
			outgoing.writeOpcode(185);
			outgoing.writeShort(id);
			Widget widget = Widget.widgets[id];

			if (widget.scripts != null && widget.scripts[0][0] == 5) {
				int operand = widget.scripts[0][1];
				if (settings[operand] != widget.anIntArray212[0]) {
					settings[operand] = widget.anIntArray212[0];
					method33(operand);
					redrawTabArea = true;
				}
			}
		}
		if (action == 225) {
			Npc npc = npcs[clicked];
			if (npc != null) {
				method85(2, 0, 1, 0, localPlayer.pathY[0], 1, 0, npc.pathY[0], localPlayer.pathX[0], false, npc.pathX[0]);
				anInt914 = super.lastClickX;
				anInt915 = super.lastClickY;
				anInt917 = 2;
				anInt916 = 0;
				anInt1226 += clicked;
				if (anInt1226 >= 85) {
					outgoing.writeOpcode(230);
					outgoing.writeByte(239);
					anInt1226 = 0;
				}
				outgoing.writeOpcode(17);
				outgoing.writeLEShortA(clicked);
			}
		}
		if (action == 965) {
			Npc npc = npcs[clicked];
			if (npc != null) {
				method85(2, 0, 1, 0, localPlayer.pathY[0], 1, 0, npc.pathY[0], localPlayer.pathX[0], false, npc.pathX[0]);
				anInt914 = super.lastClickX;
				anInt915 = super.lastClickY;
				anInt917 = 2;
				anInt916 = 0;
				anInt1134++;
				if (anInt1134 >= 96) {
					outgoing.writeOpcode(152);
					outgoing.writeByte(88);
					anInt1134 = 0;
				}
				outgoing.writeOpcode(21);
				outgoing.writeShort(clicked);
			}
		}
		if (action == 413) {
			Npc npc = npcs[clicked];
			if (npc != null) {
				method85(2, 0, 1, 0, localPlayer.pathY[0], 1, 0, npc.pathY[0], localPlayer.pathX[0], false, npc.pathX[0]);
				anInt914 = super.lastClickX;
				anInt915 = super.lastClickY;
				anInt917 = 2;
				anInt916 = 0;
				outgoing.writeOpcode(131);
				outgoing.writeLEShortA(clicked);
				outgoing.writeShortA(anInt1137);
			}
		}
		if (action == 200) {
			clearTopInterfaces();
		}
		if (action == 1025) {
			Npc npc = npcs[clicked];
			if (npc != null) {
				NpcDefinition definition = npc.definition;
				if (definition.morphisms != null) {
					definition = definition.morph();
				}
				if (definition != null) {
					String description;
					if (definition.description != null) {
						description = new String(definition.description);
					} else {
						description = "It's a " + definition.name + ".";
					}
					addChatMessage(0, description, "");
				}
			}
		}
		if (action == 900) {
			method66(clicked, id, x);
			outgoing.writeOpcode(252);
			outgoing.writeLEShortA(clicked >> 14 & 0x7fff);
			outgoing.writeLEShort(id + regionBaseY);
			outgoing.writeShortA(x + regionBaseX);
		}
		if (action == 412) { // opcode 72, note the action
			Npc npc = npcs[clicked];
			if (npc != null) {
				method85(2, 0, 1, 0, localPlayer.pathY[0], 1, 0, npc.pathY[0], localPlayer.pathX[0], false, npc.pathX[0]);
				anInt914 = super.lastClickX;
				anInt915 = super.lastClickY;
				anInt917 = 2;
				anInt916 = 0;
				outgoing.writeOpcode(72);
				outgoing.writeShortA(clicked);
			}
		}
		if (action == 365) {
			Player player = players[clicked];
			if (player != null) {
				method85(2, 0, 1, 0, localPlayer.pathY[0], 1, 0, player.pathY[0], localPlayer.pathX[0], false, player.pathX[0]);
				anInt914 = super.lastClickX;
				anInt915 = super.lastClickY;
				anInt917 = 2;
				anInt916 = 0;
				outgoing.writeOpcode(249);
				outgoing.writeShortA(clicked);
				outgoing.writeLEShort(anInt1137);
			}
		}
		if (action == 729) {
			Player player = players[clicked];
			if (player != null) {
				method85(2, 0, 1, 0, localPlayer.pathY[0], 1, 0, player.pathY[0], localPlayer.pathX[0], false, player.pathX[0]);
				anInt914 = super.lastClickX;
				anInt915 = super.lastClickY;
				anInt917 = 2;
				anInt916 = 0;
				outgoing.writeOpcode(39);
				outgoing.writeLEShort(clicked);
			}
		}
		if (action == 577) {
			Player player = players[clicked];
			if (player != null) {
				method85(2, 0, 1, 0, localPlayer.pathY[0], 1, 0, player.pathY[0], localPlayer.pathX[0], false, player.pathX[0]);
				anInt914 = super.lastClickX;
				anInt915 = super.lastClickY;
				anInt917 = 2;
				anInt916 = 0;
				outgoing.writeOpcode(139);
				outgoing.writeLEShort(clicked);
			}
		}
		if (action == 956 && method66(clicked, id, x)) {
			outgoing.writeOpcode(35);
			outgoing.writeLEShort(x + regionBaseX);
			outgoing.writeShortA(anInt1137);
			outgoing.writeShortA(id + regionBaseY);
			outgoing.writeLEShort(clicked >> 14 & 0x7fff);
		}
		if (action == 567) {
			boolean flag6 = method85(2, 0, 0, 0, localPlayer.pathY[0], 0, 0, id, localPlayer.pathX[0], false, x);
			if (!flag6) {
				flag6 = method85(2, 0, 1, 0, localPlayer.pathY[0], 1, 0, id, localPlayer.pathX[0], false, x);
			}
			anInt914 = super.lastClickX;
			anInt915 = super.lastClickY;
			anInt917 = 2;
			anInt916 = 0;
			outgoing.writeOpcode(23);
			outgoing.writeLEShort(id + regionBaseY);
			outgoing.writeLEShort(clicked);
			outgoing.writeLEShort(x + regionBaseX);
		}
		if (action == 867) {
			if ((clicked & 3) == 0) {
				anInt1175++;
			}
			if (anInt1175 >= 59) {
				outgoing.writeOpcode(200);
				outgoing.writeShort(25501);
				anInt1175 = 0;
			}
			outgoing.writeOpcode(43);
			outgoing.writeLEShort(id);
			outgoing.writeShortA(clicked);
			outgoing.writeShortA(x);
			anInt1243 = 0;
			anInt1244 = id;
			anInt1245 = x;
			anInt1246 = 2;
			if (Widget.widgets[id].parent == openInterfaceId) {
				anInt1246 = 1;
			}
			if (Widget.widgets[id].parent == backDialogueId) {
				anInt1246 = 3;
			}
		}
		if (action == 543) {
			outgoing.writeOpcode(237);
			outgoing.writeShort(x);
			outgoing.writeShortA(clicked);
			outgoing.writeShort(id);
			outgoing.writeShortA(anInt1137);
			anInt1243 = 0;
			anInt1244 = id;
			anInt1245 = x;
			anInt1246 = 2;
			if (Widget.widgets[id].parent == openInterfaceId) {
				anInt1246 = 1;
			}
			if (Widget.widgets[id].parent == backDialogueId) {
				anInt1246 = 3;
			}
		}
		if (action == 606) {
			String s2 = menuActionTexts[row];
			int j2 = s2.indexOf("@whi@");
			if (j2 != -1) {
				if (openInterfaceId == -1) {
					clearTopInterfaces();
					reportInput = s2.substring(j2 + 5).trim();
					reportAbuseMuteToggle = false;
					for (Widget element : Widget.widgets) {
						if (element == null || element.anInt214 != 600) {
							continue;
						}
						reportInterfaceId = openInterfaceId = element.parent;
						break;
					}

				} else {
					addChatMessage(0, "Please close the interface you have open before using 'report abuse'", "");
				}
			}
		}
		if (action == 491) {
			Player player = players[clicked];
			if (player != null) {
				method85(2, 0, 1, 0, localPlayer.pathY[0], 1, 0, player.pathY[0], localPlayer.pathX[0], false, player.pathX[0]);
				anInt914 = super.lastClickX;
				anInt915 = super.lastClickY;
				anInt917 = 2;
				anInt916 = 0;
				outgoing.writeOpcode(14);
				outgoing.writeShortA(anInt1284); // widget id
				outgoing.writeShort(clicked); // index
				outgoing.writeShort(anInt1285); // item id
				outgoing.writeLEShort(anInt1283); // item slot
			}
		}
		if (action == 639) {
			String s3 = menuActionTexts[row];
			int k2 = s3.indexOf("@whi@");
			if (k2 != -1) {
				long l4 = StringUtils.encodeBase37(s3.substring(k2 + 5).trim());
				int k3 = -1;
				for (int i4 = 0; i4 < friendCount; i4++) {
					if (friends[i4] != l4) {
						continue;
					}
					k3 = i4;
					break;
				}

				if (k3 != -1 && friendWorlds[k3] > 0) {
					redrawChatbox = true;
					inputDialogueState = 0;
					messagePromptRaised = true;
					messagePromptInput = "";
					friendListAction = 3;
					friendHash = friends[k3];
					aString1121 = "Enter message to send to " + friendUsernames[k3];
				}
			}
		}
		if (action == 454) {
			outgoing.writeOpcode(41);
			outgoing.writeShort(clicked);
			outgoing.writeShortA(x);
			outgoing.writeShortA(id);
			anInt1243 = 0;
			anInt1244 = id;
			anInt1245 = x;
			anInt1246 = 2;
			if (Widget.widgets[id].parent == openInterfaceId) {
				anInt1246 = 1;
			}
			if (Widget.widgets[id].parent == backDialogueId) {
				anInt1246 = 3;
			}
		}
		if (action == 478) {
			Npc npc = npcs[clicked];
			if (npc != null) {
				method85(2, 0, 1, 0, localPlayer.pathY[0], 1, 0, npc.pathY[0], localPlayer.pathX[0], false, npc.pathX[0]);
				anInt914 = super.lastClickX;
				anInt915 = super.lastClickY;
				anInt917 = 2;
				anInt916 = 0;
				if ((clicked & 3) == 0) {
					anInt1155++;
				}
				if (anInt1155 >= 53) {
					outgoing.writeOpcode(85);
					outgoing.writeByte(66);
					anInt1155 = 0;
				}
				outgoing.writeOpcode(18);
				outgoing.writeLEShort(clicked);
			}
		}
		if (action == 113) {
			method66(clicked, id, x);
			outgoing.writeOpcode(70);
			outgoing.writeLEShort(x + regionBaseX);
			outgoing.writeShort(id + regionBaseY);
			outgoing.writeLEShortA(clicked >> 14 & 0x7fff);
		}
		if (action == 872) {
			method66(clicked, id, x);
			outgoing.writeOpcode(234);
			outgoing.writeLEShortA(x + regionBaseX);
			outgoing.writeShortA(clicked >> 14 & 0x7fff);
			outgoing.writeLEShortA(id + regionBaseY);
		}
		if (action == 502) {
			method66(clicked, id, x);
			outgoing.writeOpcode(132);
			outgoing.writeLEShortA(x + regionBaseX);
			outgoing.writeShort(clicked >> 14 & 0x7fff);
			outgoing.writeShortA(id + regionBaseY);
		}
		if (action == 1125) {
			ItemDefinition class8 = ItemDefinition.lookup(clicked);
			Widget class9_4 = Widget.widgets[id];
			String s5;
			if (class9_4 != null && class9_4.inventoryAmounts[x] >= 0x186a0) {
				s5 = class9_4.inventoryAmounts[x] + " x " + class8.name;
			} else if (class8.description != null) {
				s5 = new String(class8.description);
			} else {
				s5 = "It's a " + class8.name + ".";
			}
			addChatMessage(0, s5, "");
		}
		if (action == 169) {
			outgoing.writeOpcode(185);
			outgoing.writeShort(id);
			Widget class9_3 = Widget.widgets[id];
			if (class9_3.scripts != null && class9_3.scripts[0][0] == 5) {
				int l2 = class9_3.scripts[0][1];
				settings[l2] = 1 - settings[l2];
				method33(l2);
				redrawTabArea = true;
			}
		}
		if (action == 447) {
			selectedItemId = 1;
			anInt1283 = x;
			anInt1284 = id;
			anInt1285 = clicked;
			selectedItemName = ItemDefinition.lookup(clicked).name;
			selectedSpellId = 0;
			redrawTabArea = true;
			return;
		}
		if (action == 1226) {
			int j1 = clicked >> 14 & 0x7fff;
			ObjectDefinition class46 = ObjectDefinition.lookup(j1);
			String s10;
			if (class46.description != null) {
				s10 = new String(class46.description);
			} else {
				s10 = "It's a " + class46.name + ".";
			}
			addChatMessage(0, s10, "");
		}
		if (action == 244) {
			boolean flag7 = method85(2, 0, 0, 0, localPlayer.pathY[0], 0, 0, id, localPlayer.pathX[0], false, x);
			if (!flag7) {
				flag7 = method85(2, 0, 1, 0, localPlayer.pathY[0], 1, 0, id, localPlayer.pathX[0], false, x);
			}
			anInt914 = super.lastClickX;
			anInt915 = super.lastClickY;
			anInt917 = 2;
			anInt916 = 0;
			outgoing.writeOpcode(253);
			outgoing.writeLEShort(x + regionBaseX);
			outgoing.writeLEShortA(id + regionBaseY);
			outgoing.writeShortA(clicked);
		} else if (action == 1448) {
			ItemDefinition definition = ItemDefinition.lookup(clicked);
			String description;
			if (definition.description != null) {
				description = new String(definition.description);
			} else {
				description = "It's a " + definition.name + ".";
			}
			addChatMessage(0, description, "");
		}

		selectedItemId = 0;
		selectedSpellId = 0;
		redrawTabArea = true;
	}

	public final void method71() {
		if (selectedItemId == 0 && selectedSpellId == 0) {
			menuActionTexts[menuActionRow] = "Walk here";
			menuActionIds[menuActionRow] = 516;
			anIntArray1091[menuActionRow] = super.mouseEventX;
			anIntArray1092[menuActionRow] = super.mouseEventY;
			menuActionRow++;
		}
		int previous = -1;
		for (int k = 0; k < Model.anInt1687; k++) {
			int config = Model.anIntArray1688[k];
			int x = config & 0x7f;
			int y = config >> 7 & 0x7f;
			int type = config >> 29 & 3;
			int id = config >> 14 & 0x7fff;
			if (config == previous) {
				continue;
			}
			previous = config;
			if (type == 2 && scene.getConfig(x, y, plane, config) >= 0) {
				ObjectDefinition definition = ObjectDefinition.lookup(id);
				if (definition.morphisms != null) {
					definition = definition.morph();
				}
				if (definition == null) {
					continue;
				}
				if (selectedItemId == 1) {
					menuActionTexts[menuActionRow] = "Use " + selectedItemName + " with @cya@" + definition.name;
					menuActionIds[menuActionRow] = 62;
					anIntArray1094[menuActionRow] = config;
					anIntArray1091[menuActionRow] = x;
					anIntArray1092[menuActionRow] = y;
					menuActionRow++;
				} else if (selectedSpellId == 1) {
					if ((anInt1138 & 4) == 4) {
						menuActionTexts[menuActionRow] = selectedSpellName + " @cya@" + definition.name;
						menuActionIds[menuActionRow] = 956;
						anIntArray1094[menuActionRow] = config;
						anIntArray1091[menuActionRow] = x;
						anIntArray1092[menuActionRow] = y;
						menuActionRow++;
					}
				} else {
					if (definition.interactions != null) {
						for (int action = 4; action >= 0; action--) {
							if (definition.interactions[action] != null) {
								menuActionTexts[menuActionRow] = definition.interactions[action] + " @cya@" + definition.name;
								if (action == 0) {
									menuActionIds[menuActionRow] = 502;
								}
								if (action == 1) {
									menuActionIds[menuActionRow] = 900;
								}
								if (action == 2) {
									menuActionIds[menuActionRow] = 113;
								}
								if (action == 3) {
									menuActionIds[menuActionRow] = 872;
								}
								if (action == 4) {
									menuActionIds[menuActionRow] = 1062;
								}
								anIntArray1094[menuActionRow] = config;
								anIntArray1091[menuActionRow] = x;
								anIntArray1092[menuActionRow] = y;
								menuActionRow++;
							}
						}
					}
					menuActionTexts[menuActionRow] = "Examine @cya@" + definition.name;
					menuActionIds[menuActionRow] = 1226;
					anIntArray1094[menuActionRow] = definition.id << 14;
					anIntArray1091[menuActionRow] = x;
					anIntArray1092[menuActionRow] = y;
					menuActionRow++;
				}
			}
			if (type == 1) {
				Npc npc = npcs[id];
				if (npc.definition.size == 1 && (npc.worldX & 0x7f) == 64 && (npc.worldY & 0x7f) == 64) {
					for (int j2 = 0; j2 < npcCount; j2++) {
						Npc localNpc = npcs[npcList[j2]];
						if (localNpc != null && localNpc != npc && localNpc.definition.size == 1
								&& ((Mob) localNpc).worldX == npc.worldX && ((Mob) localNpc).worldY == npc.worldY) {
							method87(localNpc.definition, npcList[j2], y, x);
						}
					}

					for (int l2 = 0; l2 < playerCount; l2++) {
						Player player = players[playerList[l2]];
						if (player != null && player.worldX == npc.worldX && player.worldY == npc.worldY) {
							method88(x, playerList[l2], player, y);
						}
					}

				}
				method87(npc.definition, id, y, x);
			}
			if (type == 0) {
				Player player = players[id];
				if ((player.worldX & 0x7f) == 64 && (player.worldY & 0x7f) == 64) {
					for (int k2 = 0; k2 < npcCount; k2++) {
						Npc npc = npcs[npcList[k2]];
						if (npc != null && npc.definition.size == 1 && npc.worldX == player.worldX && npc.worldY == player.worldY) {
							method87(npc.definition, npcList[k2], y, x);
						}
					}

					for (int i3 = 0; i3 < playerCount; i3++) {
						Player localPlayer = players[playerList[i3]];
						if (localPlayer != null && localPlayer != player && localPlayer.worldX == player.worldX
								&& localPlayer.worldY == player.worldY) {
							method88(x, playerList[i3], localPlayer, y);
						}
					}

				}
				method88(x, id, player, y);
			}
			if (type == 3) {
				Deque items = groundItems[plane][x][y];
				if (items != null) {
					for (Item item = (Item) items.getTail(); item != null; item = (Item) items.getPrevious()) {
						ItemDefinition definition = ItemDefinition.lookup(item.id);
						if (selectedItemId == 1) {
							menuActionTexts[menuActionRow] = "Use " + selectedItemName + " with @lre@" + definition.name;
							menuActionIds[menuActionRow] = 511;
							anIntArray1094[menuActionRow] = item.id;
							anIntArray1091[menuActionRow] = x;
							anIntArray1092[menuActionRow] = y;
							menuActionRow++;
						} else if (selectedSpellId == 1) {
							if ((anInt1138 & 1) == 1) {
								menuActionTexts[menuActionRow] = selectedSpellName + " @lre@" + definition.name;
								menuActionIds[menuActionRow] = 94;
								anIntArray1094[menuActionRow] = item.id;
								anIntArray1091[menuActionRow] = x;
								anIntArray1092[menuActionRow] = y;
								menuActionRow++;
							}
						} else {
							for (int j3 = 4; j3 >= 0; j3--) {
								if (definition.groundMenuActions != null && definition.groundMenuActions[j3] != null) {
									menuActionTexts[menuActionRow] = definition.groundMenuActions[j3] + " @lre@"
											+ definition.name;
									if (j3 == 0) {
										menuActionIds[menuActionRow] = 652;
									}
									if (j3 == 1) {
										menuActionIds[menuActionRow] = 567;
									}
									if (j3 == 2) {
										menuActionIds[menuActionRow] = 234;
									}
									if (j3 == 3) {
										menuActionIds[menuActionRow] = 244;
									}
									if (j3 == 4) {
										menuActionIds[menuActionRow] = 213;
									}
									anIntArray1094[menuActionRow] = item.id;
									anIntArray1091[menuActionRow] = x;
									anIntArray1092[menuActionRow] = y;
									menuActionRow++;
								} else if (j3 == 2) {
									menuActionTexts[menuActionRow] = "Take @lre@" + definition.name;
									menuActionIds[menuActionRow] = 234;
									anIntArray1094[menuActionRow] = item.id;
									anIntArray1091[menuActionRow] = x;
									anIntArray1092[menuActionRow] = y;
									menuActionRow++;
								}
							}

							menuActionTexts[menuActionRow] = "Examine @lre@" + definition.name;
							menuActionIds[menuActionRow] = 1448;
							anIntArray1094[menuActionRow] = item.id;
							anIntArray1091[menuActionRow] = x;
							anIntArray1092[menuActionRow] = y;
							menuActionRow++;
						}
					}
				}
			}
		}
	}

	public final void processInput() {
		do {
			int key = nextPressedKey();
			if (key == -1) {
				break;
			}
			if (openInterfaceId != -1 && openInterfaceId == reportInterfaceId) {
				if (key == 8 && reportInput.length() > 0) {
					reportInput = reportInput.substring(0, reportInput.length() - 1);
				}
				if ((key >= 97 && key <= 122 || key >= 65 && key <= 90 || key >= 48 && key <= 57 || key == 32)
						&& reportInput.length() < 12) {
					reportInput += (char) key;
				}
			} else if (messagePromptRaised) {
				if (key >= 32 && key <= 122 && messagePromptInput.length() < 80) {
					messagePromptInput += (char) key;
					redrawChatbox = true;
				}
				if (key == 8 && messagePromptInput.length() > 0) {
					messagePromptInput = messagePromptInput.substring(0, messagePromptInput.length() - 1);
					redrawChatbox = true;
				}
				if (key == 13 || key == 10) {
					messagePromptRaised = false;
					redrawChatbox = true;
					if (friendListAction == 1) {
						long friend = StringUtils.encodeBase37(messagePromptInput);
						addFriend(friend);
					}
					if (friendListAction == 2 && friendCount > 0) {
						long friend = StringUtils.encodeBase37(messagePromptInput);
						removeFriend(friend);
					}
					if (friendListAction == 3 && messagePromptInput.length() > 0) {
						outgoing.writeOpcode(126);
						outgoing.writeByte(0);
						int offset = outgoing.position;
						outgoing.writeLong(friendHash);
						ChatMessageCodec.encode(messagePromptInput, outgoing);
						outgoing.writeSizeByte(outgoing.position - offset);
						messagePromptInput = ChatMessageCodec.verify(messagePromptInput);
						messagePromptInput = MessageCensor.apply(messagePromptInput);
						addChatMessage(6, messagePromptInput, StringUtils.format(StringUtils.decodeBase37(friendHash)));
						if (privateChatMode == 2) {
							privateChatMode = 1;
							aBoolean1233 = true;
							outgoing.writeOpcode(95);
							outgoing.writeByte(publicChatMode);
							outgoing.writeByte(privateChatMode);
							outgoing.writeByte(tradeChatMode);
						}
					}
					if (friendListAction == 4 && ignoredCount < 100) {
						long nameHash = StringUtils.encodeBase37(messagePromptInput);
						addIgnore(nameHash);
					}
					if (friendListAction == 5 && ignoredCount > 0) {
						long nameHash = StringUtils.encodeBase37(messagePromptInput);
						removeIgnore(nameHash);
					}
				}
			} else if (inputDialogueState == 1) {
				if (key >= 48 && key <= 57 && textInput.length() < 10) {
					textInput += (char) key;
					redrawChatbox = true;
				}
				if (key == 8 && textInput.length() > 0) {
					textInput = textInput.substring(0, textInput.length() - 1);
					redrawChatbox = true;
				}
				if (key == 13 || key == 10) {
					if (textInput.length() > 0) {
						int amount = 0;
						try {
							amount = Integer.parseInt(textInput);
						} catch (Exception _ex) {
						}
						outgoing.writeOpcode(208);
						outgoing.writeInt(amount);
					}
					inputDialogueState = 0;
					redrawChatbox = true;
				}
			} else if (inputDialogueState == 2) {
				if (key >= 32 && key <= 122 && textInput.length() < 12) {
					textInput += (char) key;
					redrawChatbox = true;
				}
				if (key == 8 && textInput.length() > 0) {
					textInput = textInput.substring(0, textInput.length() - 1);
					redrawChatbox = true;
				}
				if (key == 13 || key == 10) {
					if (textInput.length() > 0) {
						outgoing.writeOpcode(60);
						outgoing.writeLong(StringUtils.encodeBase37(textInput));
					}
					inputDialogueState = 0;
					redrawChatbox = true;
				}
			} else if (backDialogueId == -1) {
				if (key >= 32 && key <= 122 && input.length() < 80) {
					input += (char) key;
					redrawChatbox = true;
				}
				if (key == 8 && input.length() > 0) {
					input = input.substring(0, input.length() - 1);
					redrawChatbox = true;
				}
				if ((key == 13 || key == 10) && input.length() > 0) {
					if (playerPrivelage == 2) {
						if (input.equals("::clientdrop")) {
							attemptReconnection();
						}
						if (input.equals("::lag")) {
							debug();
						}

						if (input.equals("::prefetchmusic")) {
							for (int file = 0; file < provider.getCount(2); file++) {
								provider.requestExtra(2, file, (byte) 1);
							}
						}
						if (input.equals("::fpson")) {
							displayFps = true;
						}
						if (input.equals("::fpsoff")) {
							displayFps = false;
						}
						if (input.equals("::noclip")) {
							for (int z = 0; z < 4; z++) {
								for (int x = 1; x < 103; x++) {
									for (int y = 1; y < 103; y++) {
										collisionMaps[z].adjacencies[x][y] = 0;
									}
								}
							}
						}
						if (input.equals("::pripos")) {
							System.out.println(localPlayer.worldX);
							System.out.println(localPlayer.worldY);
						}
					}
					if (input.startsWith("::")) {
					    if (input.equals("::frame")) {
					        System.out.println("Height: " + frameHeight);
					        System.out.println("Width: " + frameWidth);
                        }
						outgoing.writeOpcode(103);
						outgoing.writeByte(input.length() - 1);
						outgoing.writeJString(input.substring(2));
					} else {
						String s = input.toLowerCase();
						int colour = 0;
						if (s.startsWith("yellow:")) {
							colour = 0;
							input = input.substring(7);
						} else if (s.startsWith("red:")) {
							colour = 1;
							input = input.substring(4);
						} else if (s.startsWith("green:")) {
							colour = 2;
							input = input.substring(6);
						} else if (s.startsWith("cyan:")) {
							colour = 3;
							input = input.substring(5);
						} else if (s.startsWith("purple:")) {
							colour = 4;
							input = input.substring(7);
						} else if (s.startsWith("white:")) {
							colour = 5;
							input = input.substring(6);
						} else if (s.startsWith("flash1:")) {
							colour = 6;
							input = input.substring(7);
						} else if (s.startsWith("flash2:")) {
							colour = 7;
							input = input.substring(7);
						} else if (s.startsWith("flash3:")) {
							colour = 8;
							input = input.substring(7);
						} else if (s.startsWith("glow1:")) {
							colour = 9;
							input = input.substring(6);
						} else if (s.startsWith("glow2:")) {
							colour = 10;
							input = input.substring(6);
						} else if (s.startsWith("glow3:")) {
							colour = 11;
							input = input.substring(6);
						}
						s = input.toLowerCase();
						int effect = 0;
						if (s.startsWith("wave:")) {
							effect = 1;
							input = input.substring(5);
						} else if (s.startsWith("wave2:")) {
							effect = 2;
							input = input.substring(6);
						} else if (s.startsWith("shake:")) {
							effect = 3;
							input = input.substring(6);
						} else if (s.startsWith("scroll:")) {
							effect = 4;
							input = input.substring(7);
						} else if (s.startsWith("slide:")) {
							effect = 5;
							input = input.substring(6);
						}
						outgoing.writeOpcode(4);
						outgoing.writeByte(0);
						int start = outgoing.position;
						outgoing.writeByteS(effect);
						outgoing.writeByteS(colour);
						chatBuffer.position = 0;
						ChatMessageCodec.encode(input, chatBuffer);
						outgoing.writeReverseDataA(chatBuffer.payload, 0, chatBuffer.position);
						outgoing.writeSizeByte(outgoing.position - start);
						input = ChatMessageCodec.verify(input);
						input = MessageCensor.apply(input);
						localPlayer.spokenText = input;
						localPlayer.textColour = colour;
						localPlayer.textEffect = effect;
						localPlayer.textCycle = 150;
						if (playerPrivelage == 2) {
							addChatMessage(2, localPlayer.spokenText, "@cr2@" + localPlayer.name);
						} else if (playerPrivelage == 1) {
							addChatMessage(2, localPlayer.spokenText, "@cr1@" + localPlayer.name);
						} else {
							addChatMessage(2, localPlayer.spokenText, localPlayer.name);
						}
						if (publicChatMode == 2) {
							publicChatMode = 3;
							aBoolean1233 = true;
							outgoing.writeOpcode(95);
							outgoing.writeByte(publicChatMode);
							outgoing.writeByte(privateChatMode);
							outgoing.writeByte(tradeChatMode);
						}
					}
					input = "";
					redrawChatbox = true;
				}
			}
		} while (true);
	}

	public final void buildChatAreaMenu(int mouseY) {
		int scrollOffset = 0;
		for (int id = 0; id < 100; id++) {
			if (chatMessages[id] == null) {
				continue;
			}
			int type = chatTypes[id];
			int scrollPos = 70 - scrollOffset * 14 + chatboxScrollerPos + 4;
			if (scrollPos < -20) {
				break;
			}
			String name = chatNames[id];
			if (name != null && name.startsWith("@cr1@")) {
				name = name.substring(5);
			}
			if (name != null && name.startsWith("@cr2@")) {
				name = name.substring(5);
			}
			if (type == 0) {
				scrollOffset++;
			}
			if ((type == 1 || type == 2) && (type == 1 || publicChatMode == 0 || publicChatMode == 1 && displayMessageFrom(name))) {
				if (mouseY > scrollPos - 14 && mouseY <= scrollPos && !name.equals(localPlayer.name)) {
					if (playerPrivelage >= 1) {
						menuActionTexts[menuActionRow] = "Report abuse @whi@" + name;
						menuActionIds[menuActionRow] = 606;
						menuActionRow++;
					}
					menuActionTexts[menuActionRow] = "Add ignore @whi@" + name;
					menuActionIds[menuActionRow] = 42;
					menuActionRow++;
					menuActionTexts[menuActionRow] = "Add friend @whi@" + name;
					menuActionIds[menuActionRow] = 337;
					menuActionRow++;
				}
				scrollOffset++;
			}
			if ((type == 3 || type == 7) && splitPrivateChat == 0
					&& (type == 7 || privateChatMode == 0 || privateChatMode == 1 && displayMessageFrom(name))) {
				if (mouseY > scrollPos - 14 && mouseY <= scrollPos) {
					if (playerPrivelage >= 1) {
						menuActionTexts[menuActionRow] = "Report abuse @whi@" + name;
						menuActionIds[menuActionRow] = 606;
						menuActionRow++;
					}
					menuActionTexts[menuActionRow] = "Add ignore @whi@" + name;
					menuActionIds[menuActionRow] = 42;
					menuActionRow++;
					menuActionTexts[menuActionRow] = "Add friend @whi@" + name;
					menuActionIds[menuActionRow] = 337;
					menuActionRow++;
				}
				scrollOffset++;
			}
			if (type == 4 && (tradeChatMode == 0 || tradeChatMode == 1 && displayMessageFrom(name))) {
				if (mouseY > scrollPos - 14 && mouseY <= scrollPos) {
					menuActionTexts[menuActionRow] = "Accept trade @whi@" + name;
					menuActionIds[menuActionRow] = 484;
					menuActionRow++;
				}
				scrollOffset++;
			}
			if ((type == 5 || type == 6) && splitPrivateChat == 0 && privateChatMode < 2) {
				scrollOffset++;
			}
			if (type == 8 && (tradeChatMode == 0 || tradeChatMode == 1 && displayMessageFrom(name))) {
				if (mouseY > scrollPos - 14 && mouseY <= scrollPos) {
					menuActionTexts[menuActionRow] = "Accept challenge @whi@" + name;
					menuActionIds[menuActionRow] = 6;
					menuActionRow++;
				}
				scrollOffset++;
			}
		}

	}

	public final void method75(Widget widget) {
		int j = widget.anInt214;
		if (j >= 1 && j <= 100 || j >= 701 && j <= 800) {
			if (j == 1 && friendServerStatus == 0) {
				widget.hiddenText = "Loading friend list";
				widget.anInt217 = 0;
				return;
			}
			if (j == 1 && friendServerStatus == 1) {
				widget.hiddenText = "Connecting to friendserver";
				widget.anInt217 = 0;
				return;
			}
			if (j == 2 && friendServerStatus != 2) {
				widget.hiddenText = "Please wait...";
				widget.anInt217 = 0;
				return;
			}
			int count = friendCount;
			if (friendServerStatus != 2) {
				count = 0;
			}
			if (j > 700) {
				j -= 601;
			} else {
				j--;
			}
			if (j >= count) {
				widget.hiddenText = "";
				widget.anInt217 = 0;
				return;
			}
			widget.hiddenText = friendUsernames[j];
			widget.anInt217 = 1;
			return;
		}
		if (j >= 101 && j <= 200 || j >= 801 && j <= 900) {
			int l = friendCount;
			if (friendServerStatus != 2) {
				l = 0;
			}
			if (j > 800) {
				j -= 701;
			} else {
				j -= 101;
			}
			if (j >= l) {
				widget.hiddenText = "";
				widget.anInt217 = 0;
				return;
			}
			if (friendWorlds[j] == 0) {
				widget.hiddenText = "@red@Offline";
			} else if (friendWorlds[j] == nodeId) {
				widget.hiddenText = "@gre@World-" + (friendWorlds[j] - 9);
			} else {
				widget.hiddenText = "@yel@World-" + (friendWorlds[j] - 9);
			}
			widget.anInt217 = 1;
			return;
		}
		if (j == 203) {
			int i1 = friendCount;
			if (friendServerStatus != 2) {
				i1 = 0;
			}
			widget.scrollLimit = i1 * 15 + 20;
			if (widget.scrollLimit <= widget.height) {
				widget.scrollLimit = widget.height + 1;
			}
			return;
		}
		if (j >= 401 && j <= 500) {
			if ((j -= 401) == 0 && friendServerStatus == 0) {
				widget.hiddenText = "Loading ignore list";
				widget.anInt217 = 0;
				return;
			}
			if (j == 1 && friendServerStatus == 0) {
				widget.hiddenText = "Please wait...";
				widget.anInt217 = 0;
				return;
			}
			int j1 = ignoredCount;
			if (friendServerStatus == 0) {
				j1 = 0;
			}
			if (j >= j1) {
				widget.hiddenText = "";
				widget.anInt217 = 0;
				return;
			}
			widget.hiddenText = StringUtils.format(StringUtils.decodeBase37(ignoredPlayers[j]));
			widget.anInt217 = 1;
			return;
		}
		if (j == 503) {
			widget.scrollLimit = ignoredCount * 15 + 20;
			if (widget.scrollLimit <= widget.height) {
				widget.scrollLimit = widget.height + 1;
			}
			return;
		}
		if (j == 327) {
			widget.spritePitch = 150;
			widget.spriteRoll = (int) (Math.sin(tick / 40D) * 256D) & 0x7ff;
			if (avatarChanged) {
				for (int k1 = 0; k1 < 7; k1++) {
					int style = characterDesignStyles[k1];
					if (style >= 0 && !IdentityKit.kits[style].bodyLoaded()) {
						return;
					}
				}

				avatarChanged = false;
				Model[] models = new Model[7];
				int modelCount = 0;
				for (int i = 0; i < 7; i++) {
					int style = characterDesignStyles[i];
					if (style >= 0) {
						models[modelCount++] = IdentityKit.kits[style].bodyModel();
					}
				}

				Model model = new Model(modelCount, models);
				for (int part = 0; part < 5; part++) {
					if (characterDesignColours[part] != 0) {
						model.recolour(PLAYER_BODY_RECOLOURS[part][0], PLAYER_BODY_RECOLOURS[part][characterDesignColours[part]]);
						if (part == 1) {
							model.recolour(SKIN_COLOURS[0], SKIN_COLOURS[characterDesignColours[part]]);
						}
					}
				}

				model.skin();
				model.apply(Animation.animations[localPlayer.idleAnimation].primaryFrames[0]);
				model.light(64, 850, -30, -50, -30, true);
				widget.mediaType = 5;
				widget.media = 0;
				Widget.clearModels(0, 5, model);
			}
			return;
		}
		if (j == 324) {
			if (aClass30_Sub2_Sub1_Sub1_931 == null) {
				aClass30_Sub2_Sub1_Sub1_931 = widget.aClass30_Sub2_Sub1_Sub1_207;
				aClass30_Sub2_Sub1_Sub1_932 = widget.aClass30_Sub2_Sub1_Sub1_260;
			}
			if (maleAvatar) {
				widget.aClass30_Sub2_Sub1_Sub1_207 = aClass30_Sub2_Sub1_Sub1_932;
				return;
			}
			widget.aClass30_Sub2_Sub1_Sub1_207 = aClass30_Sub2_Sub1_Sub1_931;
			return;
		}
		if (j == 325) {
			if (aClass30_Sub2_Sub1_Sub1_931 == null) {
				aClass30_Sub2_Sub1_Sub1_931 = widget.aClass30_Sub2_Sub1_Sub1_207;
				aClass30_Sub2_Sub1_Sub1_932 = widget.aClass30_Sub2_Sub1_Sub1_260;
			}
			if (maleAvatar) {
				widget.aClass30_Sub2_Sub1_Sub1_207 = aClass30_Sub2_Sub1_Sub1_931;
				return;
			}
			widget.aClass30_Sub2_Sub1_Sub1_207 = aClass30_Sub2_Sub1_Sub1_932;
			return;
		}
		if (j == 600) {
			widget.hiddenText = reportInput;
			if (tick % 20 < 10) {
				widget.hiddenText += "|";
				return;
			}
			widget.hiddenText += " ";
			return;
		}
		if (j == 613) {
			if (playerPrivelage >= 1) {
				if (reportAbuseMuteToggle) {
					widget.colour = 0xff0000;
					widget.hiddenText = "Moderator option: Mute player for 48 hours: <ON>";
				} else {
					widget.colour = 0xffffff;
					widget.hiddenText = "Moderator option: Mute player for 48 hours: <OFF>";
				}
			} else {
				widget.hiddenText = "";
			}
		}
		if (j == 650 || j == 655) {
			if (lastLoginIP != 0) {
				String s;
				if (daysSinceLogin == 0) {
					s = "earlier today";
				} else if (daysSinceLogin == 1) {
					s = "yesterday";
				} else {
					s = daysSinceLogin + " days ago";
				}
				widget.hiddenText = "You last logged in " + s + " from: " + SignLink.dns;
			} else {
				widget.hiddenText = "";
			}
		}
		if (j == 651) {
			if (unreadMessageCount == 0) {
				widget.hiddenText = "0 unread messages";
				widget.colour = 0xffff00;
			}
			if (unreadMessageCount == 1) {
				widget.hiddenText = "1 unread message";
				widget.colour = 65280;
			}
			if (unreadMessageCount > 1) {
				widget.hiddenText = unreadMessageCount + " unread messages";
				widget.colour = 65280;
			}
		}
		if (j == 652) {
			if (daysSinceRecoveryChange == 201) {
				if (hasMembersCredit == 1) {
					widget.hiddenText = "@yel@This is a non-members world: @whi@Since you are a member we";
				} else {
					widget.hiddenText = "";
				}
			} else if (daysSinceRecoveryChange == 200) {
				widget.hiddenText = "You have not yet set any password recovery questions.";
			} else {
				String s1;
				if (daysSinceRecoveryChange == 0) {
					s1 = "Earlier today";
				} else if (daysSinceRecoveryChange == 1) {
					s1 = "Yesterday";
				} else {
					s1 = daysSinceRecoveryChange + " days ago";
				}
				widget.hiddenText = s1 + " you changed your recovery questions";
			}
		}
		if (j == 653) {
			if (daysSinceRecoveryChange == 201) {
				if (hasMembersCredit == 1) {
					widget.hiddenText = "@whi@recommend you use a members world instead. You may use";
				} else {
					widget.hiddenText = "";
				}
			} else if (daysSinceRecoveryChange == 200) {
				widget.hiddenText = "We strongly recommend you do so now to secure your account.";
			} else {
				widget.hiddenText = "If you do not remember making this change then cancel it immediately";
			}
		}
		if (j == 654) {
			if (daysSinceRecoveryChange == 201) {
				if (hasMembersCredit == 1) {
					widget.hiddenText = "@whi@this world but member benefits are unavailable whilst here.";
					return;
				}
				widget.hiddenText = "";
				return;
			}
			if (daysSinceRecoveryChange == 200) {
				widget.hiddenText = "Do this from the 'account management' area on our front webpage";
				return;
			}
			widget.hiddenText = "Do this from the 'account management' area on our front webpage";
		}
	}

	public final void method78() {
		if (super.lastMetaModifier == 1) {
			if (super.lastClickX >= 539 && super.lastClickX <= 573 && super.lastClickY >= 169 && super.lastClickY < 205
					&& inventoryTabIds[0] != -1) {
				redrawTabArea = true;
				tabId = 0;
				aBoolean1103 = true;
			}
			if (super.lastClickX >= 569 && super.lastClickX <= 599 && super.lastClickY >= 168 && super.lastClickY < 205
					&& inventoryTabIds[1] != -1) {
				redrawTabArea = true;
				tabId = 1;
				aBoolean1103 = true;
			}
			if (super.lastClickX >= 597 && super.lastClickX <= 627 && super.lastClickY >= 168 && super.lastClickY < 205
					&& inventoryTabIds[2] != -1) {
				redrawTabArea = true;
				tabId = 2;
				aBoolean1103 = true;
			}
			if (super.lastClickX >= 625 && super.lastClickX <= 669 && super.lastClickY >= 168 && super.lastClickY < 203
					&& inventoryTabIds[3] != -1) {
				redrawTabArea = true;
				tabId = 3;
				aBoolean1103 = true;
			}
			if (super.lastClickX >= 666 && super.lastClickX <= 696 && super.lastClickY >= 168 && super.lastClickY < 205
					&& inventoryTabIds[4] != -1) {
				redrawTabArea = true;
				tabId = 4;
				aBoolean1103 = true;
			}
			if (super.lastClickX >= 694 && super.lastClickX <= 724 && super.lastClickY >= 168 && super.lastClickY < 205
					&& inventoryTabIds[5] != -1) {
				redrawTabArea = true;
				tabId = 5;
				aBoolean1103 = true;
			}
			if (super.lastClickX >= 722 && super.lastClickX <= 756 && super.lastClickY >= 169 && super.lastClickY < 205
					&& inventoryTabIds[6] != -1) {
				redrawTabArea = true;
				tabId = 6;
				aBoolean1103 = true;
			}
			if (super.lastClickX >= 540 && super.lastClickX <= 574 && super.lastClickY >= 466 && super.lastClickY < 502
					&& inventoryTabIds[7] != -1) {
				redrawTabArea = true;
				tabId = 7;
				aBoolean1103 = true;
			}
			if (super.lastClickX >= 572 && super.lastClickX <= 602 && super.lastClickY >= 466 && super.lastClickY < 503
					&& inventoryTabIds[8] != -1) {
				redrawTabArea = true;
				tabId = 8;
				aBoolean1103 = true;
			}
			if (super.lastClickX >= 599 && super.lastClickX <= 629 && super.lastClickY >= 466 && super.lastClickY < 503
					&& inventoryTabIds[9] != -1) {
				redrawTabArea = true;
				tabId = 9;
				aBoolean1103 = true;
			}
			if (super.lastClickX >= 627 && super.lastClickX <= 671 && super.lastClickY >= 467 && super.lastClickY < 502
					&& inventoryTabIds[10] != -1) {
				redrawTabArea = true;
				tabId = 10;
				aBoolean1103 = true;
			}
			if (super.lastClickX >= 669 && super.lastClickX <= 699 && super.lastClickY >= 466 && super.lastClickY < 503
					&& inventoryTabIds[11] != -1) {
				redrawTabArea = true;
				tabId = 11;
				aBoolean1103 = true;
			}
			if (super.lastClickX >= 696 && super.lastClickX <= 726 && super.lastClickY >= 466 && super.lastClickY < 503
					&& inventoryTabIds[12] != -1) {
				redrawTabArea = true;
				tabId = 12;
				aBoolean1103 = true;
			}
			if (super.lastClickX >= 724 && super.lastClickX <= 758 && super.lastClickY >= 466 && super.lastClickY < 502
					&& inventoryTabIds[13] != -1) {
				redrawTabArea = true;
				tabId = 13;
				aBoolean1103 = true;
			}
		}
	}

	public final void method81(DirectSprite sprite, int j, int k) {
		int l = k * k + j * j;
		if (l > 4225 && l < 0x15f90) {
			int i1 = cameraYaw + anInt1209 & 0x7ff;
			int j1 = Model.SINE[i1];
			int k1 = Model.COSINE[i1];
			j1 = j1 * 256 / (anInt1170 + 256);
			k1 = k1 * 256 / (anInt1170 + 256);
			int l1 = j * j1 + k * k1 >> 16;
			int i2 = j * k1 - k * j1 >> 16;
			double d = Math.atan2(l1, i2);
			int j2 = (int) (Math.sin(d) * 63D);
			int k2 = (int) (Math.cos(d) * 57D);
			mapEdge.method353(83 - k2 - 20, 15, 20, 15, 256, 20, d, 94 + j2 + 4 - 10);
		} else {
			drawOnMinimap(sprite, k, j);
		}
	}

	public final void method82(int i) {
		if (anInt1086 != 0) {
			return;
		}
		menuActionTexts[0] = "Cancel";
		menuActionIds[0] = 1107;
		menuActionRow = 1;
		method129();
		anInt886 = 0;

		Polygon polygon = ScreenPolygonGenerator.getScreenPolygon(this);

        if (polygon.contains(new Point(mouseEventX, mouseEventY)) && !relativeMouse) {
            if (openInterfaceId != -1) {
                method29(4, Widget.widgets[openInterfaceId], super.mouseEventX, 4, super.mouseEventY, 0);
            } else {
                method71();
            }
        }

		/*if (super.mouseEventX > 4 && super.mouseEventY > 4 && super.mouseEventX < 516 && super.mouseEventY < 338) {
			if (openInterfaceId != -1) {
				method29(4, com.jagex.Widget.widgets[openInterfaceId], super.mouseEventX, 4, super.mouseEventY, 0);
			} else {
				method71();
			}
		}*/
		if (anInt886 != anInt1026) {
			anInt1026 = anInt886;
		}
		anInt886 = 0;
		if (super.mouseEventX > 553 && super.mouseEventY > 205 && super.mouseEventX < 743 && super.mouseEventY < 466) {
			if (inventoryOverlayInterfaceId != -1) {
				method29(553, Widget.widgets[inventoryOverlayInterfaceId], super.mouseEventX, 205, super.mouseEventY, 0);
			} else if (inventoryTabIds[tabId] != -1) {
				method29(553, Widget.widgets[inventoryTabIds[tabId]], super.mouseEventX, 205, super.mouseEventY, 0);
			}
		}
		if (anInt886 != anInt1048) {
			redrawTabArea = true;
			anInt1048 = anInt886;
		}
		anInt886 = 0;
		if (super.mouseEventX > 17 && super.mouseEventY > 357 && super.mouseEventX < 496 && super.mouseEventY < 453) {
			if (backDialogueId != -1) {
				method29(17, Widget.widgets[backDialogueId], super.mouseEventX, 357, super.mouseEventY, 0);
			} else if (super.mouseEventY < 434 && super.mouseEventX < 426) {
				buildChatAreaMenu(super.mouseEventY - 357);
			}
		}
		if (backDialogueId != -1 && anInt886 != anInt1039) {
			redrawChatbox = true;
			anInt1039 = anInt886;
		}
		boolean flag = false;
		packetSize += i;
		while (!flag) {
			flag = true;
			for (int j = 0; j < menuActionRow - 1; j++) {
				if (menuActionIds[j] < 1000 && menuActionIds[j + 1] > 1000) {
					String text = menuActionTexts[j];
					menuActionTexts[j] = menuActionTexts[j + 1];
					menuActionTexts[j + 1] = text;
					int k = menuActionIds[j];
					menuActionIds[j] = menuActionIds[j + 1];
					menuActionIds[j + 1] = k;
					k = anIntArray1091[j];
					anIntArray1091[j] = anIntArray1091[j + 1];
					anIntArray1091[j + 1] = k;
					k = anIntArray1092[j];
					anIntArray1092[j] = anIntArray1092[j + 1];
					anIntArray1092[j + 1] = k;
					k = anIntArray1094[j];
					anIntArray1094[j] = anIntArray1094[j + 1];
					anIntArray1094[j + 1] = k;
					flag = false;
				}
			}
		}
	}

	public final int method83(int i, int j, int k) {
		int l = 256 - k;
		return ((i & 0xff00ff) * l + (j & 0xff00ff) * k & 0xff00ff00) + ((i & 0xff00) * l + (j & 0xff00) * k & 0xff0000) >> 8;
	}

	public final boolean method85(int movementType, int orientation, int height, int type, int initialY, int width,
			int surroundings, int finalY, int initialX, boolean flag, int finalX) {
		byte mapWidth = 104;
		byte mapLength = 104;
		for (int x = 0; x < mapWidth; x++) {
			for (int y = 0; y < mapLength; y++) {
				anIntArrayArray901[x][y] = 0;
				anIntArrayArray825[x][y] = 0x5f5e0ff;
			}
		}

		int currentX = initialX;
		int currentY = initialY;
		anIntArrayArray901[initialX][initialY] = 99;
		anIntArrayArray825[initialX][initialY] = 0;
		int step = 0;
		int i4 = 0;
		waypointX[step] = initialX;
		waypointY[step++] = initialY;
		boolean reached = false;
		int waypoints = waypointX.length;
		int adjacencies[][] = collisionMaps[plane].adjacencies;
		while (i4 != step) {
			currentX = waypointX[i4];
			currentY = waypointY[i4];
			i4 = (i4 + 1) % waypoints;
			if (currentX == finalX && currentY == finalY) {
				reached = true;
				break;
			}
			if (type != 0) {
				if ((type < 5 || type == 10)
						&& collisionMaps[plane].reachedGoal(currentX, currentY, finalX, finalY, orientation, type - 1)) {
					reached = true;
					break;
				}
				if (type < 10 && collisionMaps[plane].method220(currentY, currentX, finalX, finalY, type - 1, orientation)) {
					reached = true;
					break;
				}
			}
			if (width != 0 && height != 0
					&& collisionMaps[plane].method221(currentX, currentY, finalX, finalY, height, surroundings, width)) {
				reached = true;
				break;
			}
			int l4 = anIntArrayArray825[currentX][currentY] + 1;
			if (currentX > 0 && anIntArrayArray901[currentX - 1][currentY] == 0
					&& (adjacencies[currentX - 1][currentY] & 0x1280108) == 0) {
				waypointX[step] = currentX - 1;
				waypointY[step] = currentY;
				step = (step + 1) % waypoints;
				anIntArrayArray901[currentX - 1][currentY] = 2;
				anIntArrayArray825[currentX - 1][currentY] = l4;
			}
			if (currentX < mapWidth - 1 && anIntArrayArray901[currentX + 1][currentY] == 0
					&& (adjacencies[currentX + 1][currentY] & 0x1280180) == 0) {
				waypointX[step] = currentX + 1;
				waypointY[step] = currentY;
				step = (step + 1) % waypoints;
				anIntArrayArray901[currentX + 1][currentY] = 8;
				anIntArrayArray825[currentX + 1][currentY] = l4;
			}
			if (currentY > 0 && anIntArrayArray901[currentX][currentY - 1] == 0
					&& (adjacencies[currentX][currentY - 1] & 0x1280102) == 0) {
				waypointX[step] = currentX;
				waypointY[step] = currentY - 1;
				step = (step + 1) % waypoints;
				anIntArrayArray901[currentX][currentY - 1] = 1;
				anIntArrayArray825[currentX][currentY - 1] = l4;
			}
			if (currentY < mapLength - 1 && anIntArrayArray901[currentX][currentY + 1] == 0
					&& (adjacencies[currentX][currentY + 1] & 0x1280120) == 0) {
				waypointX[step] = currentX;
				waypointY[step] = currentY + 1;
				step = (step + 1) % waypoints;
				anIntArrayArray901[currentX][currentY + 1] = 4;
				anIntArrayArray825[currentX][currentY + 1] = l4;
			}
			if (currentX > 0 && currentY > 0 && anIntArrayArray901[currentX - 1][currentY - 1] == 0
					&& (adjacencies[currentX - 1][currentY - 1] & 0x128010e) == 0
					&& (adjacencies[currentX - 1][currentY] & 0x1280108) == 0
					&& (adjacencies[currentX][currentY - 1] & 0x1280102) == 0) {
				waypointX[step] = currentX - 1;
				waypointY[step] = currentY - 1;
				step = (step + 1) % waypoints;
				anIntArrayArray901[currentX - 1][currentY - 1] = 3;
				anIntArrayArray825[currentX - 1][currentY - 1] = l4;
			}
			if (currentX < mapWidth - 1 && currentY > 0 && anIntArrayArray901[currentX + 1][currentY - 1] == 0
					&& (adjacencies[currentX + 1][currentY - 1] & 0x1280183) == 0
					&& (adjacencies[currentX + 1][currentY] & 0x1280180) == 0
					&& (adjacencies[currentX][currentY - 1] & 0x1280102) == 0) {
				waypointX[step] = currentX + 1;
				waypointY[step] = currentY - 1;
				step = (step + 1) % waypoints;
				anIntArrayArray901[currentX + 1][currentY - 1] = 9;
				anIntArrayArray825[currentX + 1][currentY - 1] = l4;
			}
			if (currentX > 0 && currentY < mapLength - 1 && anIntArrayArray901[currentX - 1][currentY + 1] == 0
					&& (adjacencies[currentX - 1][currentY + 1] & 0x1280138) == 0
					&& (adjacencies[currentX - 1][currentY] & 0x1280108) == 0
					&& (adjacencies[currentX][currentY + 1] & 0x1280120) == 0) {
				waypointX[step] = currentX - 1;
				waypointY[step] = currentY + 1;
				step = (step + 1) % waypoints;
				anIntArrayArray901[currentX - 1][currentY + 1] = 6;
				anIntArrayArray825[currentX - 1][currentY + 1] = l4;
			}
			if (currentX < mapWidth - 1 && currentY < mapLength - 1 && anIntArrayArray901[currentX + 1][currentY + 1] == 0
					&& (adjacencies[currentX + 1][currentY + 1] & 0x12801e0) == 0
					&& (adjacencies[currentX + 1][currentY] & 0x1280180) == 0
					&& (adjacencies[currentX][currentY + 1] & 0x1280120) == 0) {
				waypointX[step] = currentX + 1;
				waypointY[step] = currentY + 1;
				step = (step + 1) % waypoints;
				anIntArrayArray901[currentX + 1][currentY + 1] = 12;
				anIntArrayArray825[currentX + 1][currentY + 1] = l4;
			}
		}
		anInt1264 = 0;
		if (!reached) {
			if (flag) {
				int i5 = 100;
				for (int delta = 1; delta < 2; delta++) {
					for (int x = finalX - delta; x <= finalX + delta; x++) {
						for (int y = finalY - delta; y <= finalY + delta; y++) {
							if (x >= 0 && y >= 0 && x < 104 && y < 104 && anIntArrayArray825[x][y] < i5) {
								i5 = anIntArrayArray825[x][y];
								currentX = x;
								currentY = y;
								anInt1264 = 1;
								reached = true;
							}
						}
					}

					if (reached) {
						break;
					}
				}
			}
			if (!reached) {
				return false;
			}
		}
		i4 = 0;
		waypointX[i4] = currentX;
		waypointY[i4++] = currentY;
		int l5;
		for (int j5 = l5 = anIntArrayArray901[currentX][currentY]; currentX != initialX || currentY != initialY; j5 = anIntArrayArray901[currentX][currentY]) {
			if (j5 != l5) {
				l5 = j5;
				waypointX[i4] = currentX;
				waypointY[i4++] = currentY;
			}
			if ((j5 & 2) != 0) {
				currentX++;
			} else if ((j5 & 8) != 0) {
				currentX--;
			}
			if ((j5 & 1) != 0) {
				currentY++;
			} else if ((j5 & 4) != 0) {
				currentY--;
			}
		}

		if (i4 > 0) {
			int waypointCount = i4;
			if (waypointCount > 25) {
				waypointCount = 25;
			}
			i4--;
			int x = waypointX[i4];
			int y = waypointY[i4];
			anInt1288 += waypointCount;
			if (anInt1288 >= 92) {
				outgoing.writeOpcode(36);
				outgoing.writeInt(0);
				anInt1288 = 0;
			}
			if (movementType == 0) {
				outgoing.writeOpcode(164);
				outgoing.writeByte(waypointCount + waypointCount + 3);
			}
			if (movementType == 1) {
				outgoing.writeOpcode(248);
				outgoing.writeByte(waypointCount + waypointCount + 3 + 14);
			}
			if (movementType == 2) {
				outgoing.writeOpcode(98);
				outgoing.writeByte(waypointCount + waypointCount + 3);
			}
			outgoing.writeLEShortA(x + regionBaseX);
			destinationX = waypointX[0];
			destinationY = waypointY[0];
			for (int j7 = 1; j7 < waypointCount; j7++) {
				i4--;
				outgoing.writeByte(waypointX[i4] - x);
				outgoing.writeByte(waypointY[i4] - y);
			}

			outgoing.writeLEShort(y + regionBaseY);
			outgoing.writeNegByte(super.keyStatuses[5] != 1 ? 0 : 1);
			return true;
		}
		return movementType != 1;
	}

	public final void method87(NpcDefinition definition, int i, int j, int k) {
		if (menuActionRow >= 400) {
			return;
		}
		if (definition.morphisms != null) {
			definition = definition.morph();
		}

		if (definition == null) {
			return;
		} else if (!definition.clickable) {
			return;
		}

		String text = definition.name;
		if (definition.combat != 0) {
			text = text + getCombatLevelColour(localPlayer.combat, definition.combat) + " (level-" + definition.combat + ")";
		}

		if (selectedItemId == 1) {
			menuActionTexts[menuActionRow] = "Use " + selectedItemName + " with @yel@" + text;
			menuActionIds[menuActionRow] = 582;
			anIntArray1094[menuActionRow] = i;
			anIntArray1091[menuActionRow] = k;
			anIntArray1092[menuActionRow] = j;
			menuActionRow++;
			return;
		}

		if (selectedSpellId == 1) {
			if ((anInt1138 & 2) == 2) {
				menuActionTexts[menuActionRow] = selectedSpellName + " @yel@" + text;
				menuActionIds[menuActionRow] = 413;
				anIntArray1094[menuActionRow] = i;
				anIntArray1091[menuActionRow] = k;
				anIntArray1092[menuActionRow] = j;
				menuActionRow++;
				return;
			}
		} else {
			if (definition.interactions != null) {
				for (int index = 4; index >= 0; index--) {
					if (definition.interactions[index] != null && !definition.interactions[index].equalsIgnoreCase("attack")) {
						menuActionTexts[menuActionRow] = definition.interactions[index] + " @yel@" + text;

						if (index == 0) {
							menuActionIds[menuActionRow] = 20; // opcode 155 - first action
						} else if (index == 1) {
							menuActionIds[menuActionRow] = 412; // opcode 72 - second action
						} else if (index == 2) {
							menuActionIds[menuActionRow] = 225; // opcode 17 - third action
						} else if (index == 3) {
							menuActionIds[menuActionRow] = 965; // opcode 21 - fourth action
						} else if (index == 4) {
							menuActionIds[menuActionRow] = 478; // opcode 18 - fifth action
						}

						anIntArray1094[menuActionRow] = i;
						anIntArray1091[menuActionRow] = k;
						anIntArray1092[menuActionRow] = j;
						menuActionRow++;
					}
				}
			}

			if (definition.interactions != null) {
				for (int index = 4; index >= 0; index--) {
					if (definition.interactions[index] != null && definition.interactions[index].equalsIgnoreCase("attack")) {
						int offset = (definition.combat > localPlayer.combat) ? 2000 : 0;

						menuActionTexts[menuActionRow] = definition.interactions[index] + " @yel@" + text;

						if (index == 0) {
							menuActionIds[menuActionRow] = 20 + offset;
						} else if (index == 1) {
							menuActionIds[menuActionRow] = 412 + offset;
						} else if (index == 2) {
							menuActionIds[menuActionRow] = 225 + offset;
						} else if (index == 3) {
							menuActionIds[menuActionRow] = 965 + offset;
						} else if (index == 4) {
							menuActionIds[menuActionRow] = 478 + offset;
						}

						anIntArray1094[menuActionRow] = i;
						anIntArray1091[menuActionRow] = k;
						anIntArray1092[menuActionRow] = j;
						menuActionRow++;
					}
				}
			}

			menuActionTexts[menuActionRow] = "Examine @yel@" + text;
			menuActionIds[menuActionRow] = 1025;
			anIntArray1094[menuActionRow] = i;
			anIntArray1091[menuActionRow] = k;
			anIntArray1092[menuActionRow] = j;
			menuActionRow++;
		}
	}

	public final void method88(int i, int j, Player player, int k) {
		if (player == localPlayer) {
			return;
		}

		if (menuActionRow >= 400) {
			return;
		}

		String text;
		if (player.skill == 0) {
			text = player.name + getCombatLevelColour(localPlayer.combat, player.combat) + " (level-" + player.combat + ")";
		} else {
			text = player.name + " (skill-" + player.skill + ")";
		}

		if (selectedItemId == 1) {
			menuActionTexts[menuActionRow] = "Use " + selectedItemName + " with @whi@" + text;
			menuActionIds[menuActionRow] = 491;
			anIntArray1094[menuActionRow] = j;
			anIntArray1091[menuActionRow] = i;
			anIntArray1092[menuActionRow] = k;
			menuActionRow++;
		} else if (selectedSpellId == 1) {
			if ((anInt1138 & 8) == 8) {
				menuActionTexts[menuActionRow] = selectedSpellName + " @whi@" + text;
				menuActionIds[menuActionRow] = 365;
				anIntArray1094[menuActionRow] = j;
				anIntArray1091[menuActionRow] = i;
				anIntArray1092[menuActionRow] = k;
				menuActionRow++;
			}
		} else {
			for (int index = 4; index >= 0; index--) {
				if (aStringArray1127[index] != null) {
					menuActionTexts[menuActionRow] = aStringArray1127[index] + " @whi@" + text;
					int offset = 0;
					if (aStringArray1127[index].equalsIgnoreCase("attack")) {
						if (player.combat > localPlayer.combat) {
							offset = 2000;
						}

						if (localPlayer.team != 0 && player.team != 0) {
							if (localPlayer.team == player.team) {
								offset = 2000;
							} else {
								offset = 0;
							}
						}
					} else if (aBooleanArray1128[index]) {
						offset = 2000;
					}

					if (index == 0) {
						menuActionIds[menuActionRow] = 561 + offset;
					} else if (index == 1) {
						menuActionIds[menuActionRow] = 779 + offset;
					} else if (index == 2) {
						menuActionIds[menuActionRow] = 27 + offset;
					} else if (index == 3) {
						menuActionIds[menuActionRow] = 577 + offset;
					} else if (index == 4) {
						menuActionIds[menuActionRow] = 729 + offset;
					}

					anIntArray1094[menuActionRow] = j;
					anIntArray1091[menuActionRow] = i;
					anIntArray1092[menuActionRow] = k;
					menuActionRow++;
				}
			}
		}

		for (int index = 0; index < menuActionRow; index++) {
			if (menuActionIds[index] == 516) {
				menuActionTexts[index] = "Walk here @whi@" + text;
				return;
			}
		}
	}

	public final void method90() {
		for (int index = 0; index < trackCount; index++) {
			if (trackDelays[index] <= 0) {
				boolean replay = false;

				try {
					if (tracks[index] == anInt874 && trackLoops[index] == anInt1289) {
						if (!waveReplay()) {
							replay = true;
						}
					} else {
						Buffer buffer = Track.data(trackLoops[index], tracks[index]);
						if (System.currentTimeMillis() + (buffer.position / 22) > aLong1172 + (anInt1257 / 22)) {
							anInt1257 = buffer.position;
							aLong1172 = System.currentTimeMillis();

							if (waveSave(buffer.payload, buffer.position)) {
								anInt874 = tracks[index];
								anInt1289 = trackLoops[index];
							} else {
								replay = true;
							}
						}
					}
				} catch (Exception exception) {
				}

				if (!replay || trackDelays[index] == -5) {
					trackCount--;
					for (int next = index; next < trackCount; next++) {
						tracks[next] = tracks[next + 1];
						trackLoops[next] = trackLoops[next + 1];
						trackDelays[next] = trackDelays[next + 1];
					}

					index--;
				} else {
					trackDelays[index] = -5;
				}
			} else {
				trackDelays[index]--;
			}
		}

		if (songDelay > 0) {
			songDelay -= 20;
			if (songDelay < 0) {
				songDelay = 0;
			}

			if (songDelay == 0 && playingMusic && !lowMemory) {
				musicId = nextMusicId;
				fadeMusic = true;
				provider.provide(2, musicId);
			}
		}
	}

	public final void method92() {
		if (minimapState != 0) {
			return;
		}

		if (super.lastMetaModifier == 1) {
			int x = super.lastClickX - 25 - 550;
			int y = super.lastClickY - 5 - 4;
			if (x >= 0 && y >= 0 && x < 146 && y < 151) {
				x -= 73;
				y -= 75;
				int angle = cameraYaw + anInt1209 & 0x7ff;
				int i1 = Rasterizer.SINE[angle];
				int j1 = Rasterizer.COSINE[angle];
				i1 = i1 * (anInt1170 + 256) >> 8;
				j1 = j1 * (anInt1170 + 256) >> 8;
				int k1 = y * i1 + x * j1 >> 11;
				int l1 = y * j1 - x * i1 >> 11;
				int i2 = localPlayer.worldX + k1 >> 7;
				int j2 = localPlayer.worldY - l1 >> 7;
				boolean flag1 = method85(1, 0, 0, 0, localPlayer.pathY[0], 0, 0, j2, localPlayer.pathX[0], true, i2);
				if (flag1) {
					outgoing.writeByte(x);
					outgoing.writeByte(y);
					outgoing.writeShort(cameraYaw);
					outgoing.writeByte(57);
					outgoing.writeByte(anInt1209);
					outgoing.writeByte(anInt1170);
					outgoing.writeByte(89);
					outgoing.writeShort(localPlayer.worldX);
					outgoing.writeShort(localPlayer.worldY);
					outgoing.writeByte(anInt1264);
					outgoing.writeByte(63);
				}
			}

			anInt1117++;
			if (anInt1117 > 1151) {
				anInt1117 = 0;
				outgoing.writeOpcode(246);
				outgoing.writeByte(0);
				int l = outgoing.position;
				if ((int) (Math.random() * 2D) == 0) {
					outgoing.writeByte(101);
				}
				outgoing.writeByte(197);
				outgoing.writeShort((int) (Math.random() * 65536D));
				outgoing.writeByte((int) (Math.random() * 256D));
				outgoing.writeByte(67);
				outgoing.writeShort(14214);
				if ((int) (Math.random() * 2D) == 0) {
					outgoing.writeShort(29487);
				}
				outgoing.writeShort((int) (Math.random() * 65536D));
				if ((int) (Math.random() * 2D) == 0) {
					outgoing.writeByte(220);
				}
				outgoing.writeByte(180);
				outgoing.writeSizeByte(outgoing.position - l);
			}
		}
	}

	public final void midiSave(byte[] data, boolean fade) {
		SignLink.midiFade = fade ? 1 : 0;
		SignLink.midiSave(data, data.length);
	}

	public final void nextForcedMovementStep(Mob character) {
		if (character.endForceMovement == tick
				|| character.emoteAnimation == -1
				|| character.animationDelay != 0
				|| character.anInt1528 + 1 > Animation.animations[character.emoteAnimation]
						.duration(character.displayedEmoteFrames)) {
			int remaining = character.endForceMovement - character.startForceMovement;
			int elapsed = tick - character.startForceMovement;
			int absInitialX = character.initialX * 128 + character.size * 64;
			int absInitialY = character.initialY * 128 + character.size * 64;
			int absDestinationX = character.destinationX * 128 + character.size * 64;
			int absDestinationY = character.destinationY * 128 + character.size * 64;
			character.worldX = (absInitialX * (remaining - elapsed) + absDestinationX * elapsed) / remaining;
			character.worldY = (absInitialY * (remaining - elapsed) + absDestinationY * elapsed) / remaining;
		}
		character.anInt1503 = 0;
		if (character.direction == 0) {
			character.nextStepOrientation = 1024;
		}
		if (character.direction == 1) {
			character.nextStepOrientation = 1536;
		}
		if (character.direction == 2) {
			character.nextStepOrientation = 0;
		}
		if (character.direction == 3) {
			character.nextStepOrientation = 512;
		}
		character.orientation = character.nextStepOrientation;
	}

	public final void nextPreForcedStep(Mob character) {
		int remaining = character.startForceMovement - tick;
		int x = character.initialX * 128 + character.size * 64;
		int y = character.initialY * 128 + character.size * 64;
		character.worldX += (x - character.worldX) / remaining;
		character.worldY += (y - character.worldY) / remaining;
		character.anInt1503 = 0;
		if (character.direction == 0) {
			character.nextStepOrientation = 1024;
		}
		if (character.direction == 1) {
			character.nextStepOrientation = 1536;
		}
		if (character.direction == 2) {
			character.nextStepOrientation = 0;
		}
		if (character.direction == 3) {
			character.nextStepOrientation = 512;
		}
	}

	public final void nextStep(Mob mob) {
		mob.movementAnimation = mob.idleAnimation;
		if (mob.remainingPath == 0) {
			mob.anInt1503 = 0;
			return;
		}
		if (mob.emoteAnimation != -1 && mob.animationDelay == 0) {
			Animation animation = Animation.animations[mob.emoteAnimation];
			if (mob.anInt1542 > 0 && animation.animatingPrecedence == 0) {
				mob.anInt1503++;
				return;
			}
			if (mob.anInt1542 <= 0 && animation.walkingPrecedence == 0) {
				mob.anInt1503++;
				return;
			}
		}
		int x = mob.worldX;
		int y = mob.worldY;
		int nextX = mob.pathX[mob.remainingPath - 1] * 128 + mob.size * 64;
		int nextY = mob.pathY[mob.remainingPath - 1] * 128 + mob.size * 64;
		if (nextX - x > 256 || nextX - x < -256 || nextY - y > 256 || nextY - y < -256) {
			mob.worldX = nextX;
			mob.worldY = nextY;
			return;
		}

		if (x < nextX) {
			if (y < nextY) {
				mob.nextStepOrientation = 0x500;
			} else if (y > nextY) {
				mob.nextStepOrientation = 0x700;
			} else {
				mob.nextStepOrientation = 0x600;
			}
		} else if (x > nextX) {
			if (y < nextY) {
				mob.nextStepOrientation = 0x300;
			} else if (y > nextY) {
				mob.nextStepOrientation = 0x100;
			} else {
				mob.nextStepOrientation = 0x200;
			}
		} else if (y < nextY) {
			mob.nextStepOrientation = 0x400;
		} else {
			mob.nextStepOrientation = 0;
		}

		int rotation = mob.nextStepOrientation - mob.orientation & 0x7ff;
		if (rotation > 0x400) {
			rotation -= 0x800;
		}

		int animation = mob.halfTurnAnimation;
		if (rotation >= -0x100 && rotation <= 0x100) {
			animation = mob.walkingAnimation;
		} else if (rotation >= 0x100 && rotation < 0x300) {
			animation = mob.quarterAnticlockwiseTurnAnimation;
		} else if (rotation >= -0x300 && rotation <= -0x100) {
			animation = mob.quarterClockwiseTurnAnimation;
		}
		if (animation == -1) {
			animation = mob.walkingAnimation;
		}

		mob.movementAnimation = animation;
		int positionDelta = 4;
		if (mob.orientation != mob.nextStepOrientation && mob.interactingMob == -1 && mob.rotation != 0) {
			positionDelta = 2;
		}
		if (mob.remainingPath > 2) {
			positionDelta = 6;
		}
		if (mob.remainingPath > 3) {
			positionDelta = 8;
		}
		if (mob.anInt1503 > 0 && mob.remainingPath > 1) {
			positionDelta = 8;
			mob.anInt1503--;
		}
		if (mob.pathRun[mob.remainingPath - 1]) {
			positionDelta *= 2;
		}
		if (positionDelta >= 8 && mob.movementAnimation == mob.walkingAnimation && mob.runAnimation != -1) {
			mob.movementAnimation = mob.runAnimation;
		}

		if (x < nextX) {
			mob.worldX += positionDelta;
			if (mob.worldX > nextX) {
				mob.worldX = nextX;
			}
		} else if (x > nextX) {
			mob.worldX -= positionDelta;
			if (mob.worldX < nextX) {
				mob.worldX = nextX;
			}
		}
		if (y < nextY) {
			mob.worldY += positionDelta;
			if (mob.worldY > nextY) {
				mob.worldY = nextY;
			}
		} else if (y > nextY) {
			mob.worldY -= positionDelta;
			if (mob.worldY < nextY) {
				mob.worldY = nextY;
			}
		}
		if (mob.worldX == nextX && mob.worldY == nextY) {
			mob.remainingPath--;
			if (mob.anInt1542 > 0) {
				mob.anInt1542--;
			}
		}
	}

	public final Socket openSocket(int port) throws IOException {
		if (SignLink.applet != null) {
			return SignLink.openSocket(port);
		}

		return new Socket(InetAddress.getByName(getCodeBase().getHost()), port);
	}

	public final boolean parseFrame() {
		if (primary == null) {
			return false;
		}

		try {
			int available = primary.available();
			if (available == 0) {
				return false;
			}

			if (opcode == -1) {
				primary.read(incomingBuffer.payload, 0, 1);
				opcode = incomingBuffer.payload[0] & 0xff;
				if (encryption != null) {
					opcode = opcode - encryption.nextKey() & 0xff;
				}
				packetSize = PacketConstants.PACKET_LENGTHS[opcode];
				available--;
			}

			if (packetSize == -1) {
				if (available > 0) {
					primary.read(incomingBuffer.payload, 0, 1);
					packetSize = incomingBuffer.payload[0] & 0xff;
					available--;
				} else {
					return false;
				}
			}

			if (packetSize == -2) {
				if (available > 1) {
					primary.read(incomingBuffer.payload, 0, 2);
					incomingBuffer.position = 0;
					packetSize = incomingBuffer.readUShort();
					available -= 2;
				} else {
					return false;
				}
			}

			if (available < packetSize) {
				return false;
			}

			incomingBuffer.position = 0;
			primary.read(incomingBuffer.payload, 0, packetSize);
			timeoutCounter = 0;
			thirdLastOpcode = secondLastOpcode;
			secondLastOpcode = lastOpcode;
			lastOpcode = opcode;

			if (opcode == 81) {
				synchronizePlayers(packetSize, incomingBuffer);
				validLocalMap = false;
				opcode = -1;
				return true;
			}
			if (opcode == 176) {
				daysSinceRecoveryChange = incomingBuffer.readNegUByte();
				unreadMessageCount = incomingBuffer.readUShortA();
				hasMembersCredit = incomingBuffer.readUByte();
				lastLoginIP = incomingBuffer.readIMEInt();
				daysSinceLogin = incomingBuffer.readUShort();
				if (lastLoginIP != 0 && openInterfaceId == -1) {
					SignLink.dnsLookup(StringUtils.decodeIp(lastLoginIP));
					clearTopInterfaces();
					char c = '\u028A';
					if (daysSinceRecoveryChange != 201 || hasMembersCredit == 1) {
						c = '\u028F';
					}
					reportInput = "";
					reportAbuseMuteToggle = false;
					for (Widget widget : Widget.widgets) {
						if (widget == null || widget.anInt214 != c) {
							continue;
						}
						openInterfaceId = widget.parent;
						break;
					}

				}
				opcode = -1;
				return true;
			}
			if (opcode == 64) {
				localX = incomingBuffer.readNegUByte();
				localY = incomingBuffer.readUByteS();
				for (int x = localX; x < localX + 8; x++) {
					for (int y = localY; y < localY + 8; y++) {
						if (groundItems[plane][x][y] != null) {
							groundItems[plane][x][y] = null;
							processGroundItems(x, y);
						}
					}
				}

				for (ObjectSpawn object = (ObjectSpawn) spawns.getFront(); object != null; object = (ObjectSpawn) spawns
						.getNext()) {
					if (object.x >= localX && object.x < localX + 8 && object.y >= localY && object.y < localY + 8
							&& object.plane == plane) {
						object.longetivity = 0;
					}
				}

				opcode = -1;
				return true;
			}
			if (opcode == 185) {
				int id = incomingBuffer.readLEUShortA();
				Widget.widgets[id].mediaType = 3;
				if (localPlayer.npcDefinition == null) {
					Widget.widgets[id].media = (localPlayer.appearanceColours[0] << 25)
							+ (localPlayer.appearanceColours[4] << 20) + (localPlayer.appearanceModels[0] << 15)
							+ (localPlayer.appearanceModels[8] << 10) + (localPlayer.appearanceModels[11] << 5)
							+ localPlayer.appearanceModels[1];
				} else {
					Widget.widgets[id].media = (int) (0x12345678L + localPlayer.npcDefinition.id);
				}
				opcode = -1;
				return true;
			}
			if (opcode == 107) {
				oriented = false;
				for (int l = 0; l < 5; l++) {
					aBooleanArray876[l] = false;
				}

				opcode = -1;
				return true;
			}
			if (opcode == 72) {
				int id = incomingBuffer.readLEUShort();
				Widget widget = Widget.widgets[id];
				for (int slot = 0; slot < widget.inventoryIds.length; slot++) {
					widget.inventoryIds[slot] = -1;
					widget.inventoryIds[slot] = 0;
				}

				opcode = -1;
				return true;
			}
			if (opcode == 214) {
				ignoredCount = packetSize / 8;
				for (int i = 0; i < ignoredCount; i++) {
					ignoredPlayers[i] = incomingBuffer.readLong();
				}

				opcode = -1;
				return true;
			}
			if (opcode == 166) {
				oriented = true;
				anInt1098 = incomingBuffer.readUByte(); // local X
				anInt1099 = incomingBuffer.readUByte(); // local Y
				anInt1100 = incomingBuffer.readUShort(); // height offset
				anInt1101 = incomingBuffer.readUByte();
				anInt1102 = incomingBuffer.readUByte();

				if (anInt1102 >= 100) {
					anInt858 = anInt1098 * 128 + 64; // absolute x
					anInt860 = anInt1099 * 128 + 64; // absolute y
					anInt859 = method42(anInt858, anInt860, plane) - anInt1100; // height
				}
				opcode = -1;
				return true;
			}
			if (opcode == 134) {
				redrawTabArea = true;
				int skill = incomingBuffer.readUByte();
				int experience = incomingBuffer.readMEInt();
				int level = incomingBuffer.readUByte();
				skillExperience[skill] = experience;
				skillLevel[skill] = level;
				levelBase[skill] = 1;
				for (int i = 0; i < 98; i++) {
					if (experience >= SKILL_EXPERIENCE[i]) {
						levelBase[skill] = i + 2;
					}
				}

				opcode = -1;
				return true;
			}
			if (opcode == 71) {
				int id = incomingBuffer.readUShort();
				int slot = incomingBuffer.readUByteA();
				if (id == 65535) {
					id = -1;
				}
				inventoryTabIds[slot] = id;
				redrawTabArea = true;
				aBoolean1103 = true;
				opcode = -1;
				return true;
			}
			if (opcode == 74) {
				int music = incomingBuffer.readLEUShort();
				if (music == 65535) {
					music = -1;
				}
				if (music != nextMusicId && playingMusic && !lowMemory && songDelay == 0) {
					musicId = music;
					fadeMusic = true;
					provider.provide(2, musicId);
				}
				nextMusicId = music;
				opcode = -1;
				return true;
			}
			if (opcode == 121) {
				int id = incomingBuffer.readLEUShortA();
				int delay = incomingBuffer.readUShortA();
				if (playingMusic && !lowMemory) {
					musicId = id;
					fadeMusic = false;
					provider.provide(2, musicId);
					songDelay = delay;
				}
				opcode = -1;
				return true;
			}
			if (opcode == 109) {
				reset();
				opcode = -1;
				return false;
			}
			if (opcode == 70) {
				int horizontalOffset = incomingBuffer.readShort();
				int verticalOffset = incomingBuffer.readLEShort();
				int id = incomingBuffer.readLEUShort();
				Widget widget = Widget.widgets[id];
				widget.horizontalDrawOffset = horizontalOffset;
				widget.verticalDrawOffset = verticalOffset;
				opcode = -1;
				return true;
			}
			if (opcode == 73 || opcode == 241) {
				int sectorX = this.sectorX;
				int sectorY = this.sectorY;
				if (opcode == 73) {
					sectorX = incomingBuffer.readUShortA();
					sectorY = incomingBuffer.readUShort();
					constructedViewport = false;
				}
				if (opcode == 241) {
					sectorY = incomingBuffer.readUShortA();
					incomingBuffer.enableBitAccess();
					for (int z = 0; z < 4; z++) {
						for (int x = 0; x < 13; x++) {
							for (int y = 0; y < 13; y++) {
								int visible = incomingBuffer.readBits(1);
								if (visible == 1) {
									localSectors[z][x][y] = incomingBuffer.readBits(26);
								} else {
									localSectors[z][x][y] = -1;
								}
							}
						}
					}

					incomingBuffer.disableBitAccess();
					sectorX = incomingBuffer.readUShort();
					constructedViewport = true;
				}
				if (this.sectorX == sectorX && this.sectorY == sectorY && loadingStage == 2) {
					opcode = -1;
					return true;
				}
				this.sectorX = sectorX;
				this.sectorY = sectorY;
				regionBaseX = (this.sectorX - 6) * 8;
				regionBaseY = (this.sectorY - 6) * 8;
				inPlayerOwnedHouse = false;
				if ((this.sectorX / 8 == 48 || this.sectorX / 8 == 49) && this.sectorY / 8 == 48) {
					inPlayerOwnedHouse = true;
				}
				if (this.sectorX / 8 == 48 && this.sectorY / 8 == 148) {
					inPlayerOwnedHouse = true;
				}
				loadingStage = 1;
				loadingStartTime = System.currentTimeMillis();
				aClass15_1165.initializeRasterizer();
				frameFont.renderCentre(257, 151, "Loading - please wait.", 0);
				frameFont.renderCentre(256, 150, "Loading - please wait.", 0xffffff);
				aClass15_1165.drawGameImage(super.graphics, 4, 4);
				if (opcode == 73) {
					int regionCount = 0;
					for (int x = (this.sectorX - 6) / 8; x <= (this.sectorX + 6) / 8; x++) {
						for (int y = (this.sectorY - 6) / 8; y <= (this.sectorY + 6) / 8; y++) {
							regionCount++;
						}
					}

					localRegionMapData = new byte[regionCount][];
					localRegionLandscapeData = new byte[regionCount][];
					localRegionIds = new int[regionCount];
					localRegionMapIds = new int[regionCount];
					localRegionLandscapeIds = new int[regionCount];
					regionCount = 0;
					for (int x = (this.sectorX - 6) / 8; x <= (this.sectorX + 6) / 8; x++) {
						for (int y = (this.sectorY - 6) / 8; y <= (this.sectorY + 6) / 8; y++) {
							localRegionIds[regionCount] = (x << 8) + y;
							if (inPlayerOwnedHouse && (y == 49 || y == 149 || y == 147 || x == 50 || x == 49 && y == 47)) {
								localRegionMapIds[regionCount] = -1;
								localRegionLandscapeIds[regionCount] = -1;
								regionCount++;
							} else {
								int map = localRegionMapIds[regionCount] = provider.resolve(x, y, 0);
								if (map != -1) {
									provider.provide(3, map);
								}
								int landscape = localRegionLandscapeIds[regionCount] = provider.resolve(x, y, 1);
								if (landscape != -1) {
									provider.provide(3, landscape);
								}
								regionCount++;
							}
						}
					}
				}

				if (opcode == 241) {
					int regionCount = 0;
					int regionIds[] = new int[676];
					for (int z = 0; z < 4; z++) {
						for (int x = 0; x < 13; x++) {
							for (int y = 0; y < 13; y++) {
								int data = localSectors[z][x][y];
								if (data != -1) {
									int secX = data >> 14 & 0x3ff;
									int secY = data >> 3 & 0x7ff;
									int region = (secX / 8 << 8) + secY / 8;
									for (int index = 0; index < regionCount; index++) {
										if (regionIds[index] != region) {
											continue;
										}
										region = -1;
										break;
									}

									if (region != -1) {
										regionIds[regionCount++] = region;
									}
								}
							}
						}
					}

					localRegionMapData = new byte[regionCount][];
					localRegionLandscapeData = new byte[regionCount][];
					localRegionIds = new int[regionCount];
					localRegionMapIds = new int[regionCount];
					localRegionLandscapeIds = new int[regionCount];
					for (int index = 0; index < regionCount; index++) {
						int id = localRegionIds[index] = regionIds[index];
						int regionX = id >> 8 & 0xff;
						int regionY = id & 0xff;
						int map = localRegionMapIds[index] = provider.resolve(regionX, regionY, 0);
						if (map != -1) {
							provider.provide(3, map);
						}
						int landscape = localRegionLandscapeIds[index] = provider.resolve(regionX, regionY, 1);
						if (landscape != -1) {
							provider.provide(3, landscape);
						}
					}
				}
				int dx = regionBaseX - previousAbsoluteX;
				int dy = regionBaseY - previousAbsoluteY;
				previousAbsoluteX = regionBaseX;
				previousAbsoluteY = regionBaseY;
				for (int index = 0; index < 16384; index++) {
					Npc npc = npcs[index];
					if (npc != null) {
						for (int point = 0; point < 10; point++) {
							npc.pathX[point] -= dx;
							npc.pathY[point] -= dy;
						}

						npc.worldX -= dx * 128;
						npc.worldY -= dy * 128;
					}
				}

				for (int index = 0; index < maximumPlayers; index++) {
					Player player = players[index];
					if (player != null) {
						for (int point = 0; point < 10; point++) {
							player.pathX[point] -= dx;
							player.pathY[point] -= dy;
						}

						player.worldX -= dx * 128;
						player.worldY -= dy * 128;
					}
				}

				validLocalMap = true;
				byte startX = 0;
				byte endX = 104;
				byte stepX = 1;
				if (dx < 0) {
					startX = 103;
					endX = -1;
					stepX = -1;
				}
				byte startY = 0;
				byte endY = 104;
				byte stepY = 1;
				if (dy < 0) {
					startY = 103;
					endY = -1;
					stepY = -1;
				}
				for (int x = startX; x != endX; x += stepX) {
					for (int y = startY; y != endY; y += stepY) {
						int shiftedX = x + dx;
						int shiftedY = y + dy;
						for (int plane = 0; plane < 4; plane++) {
							if (shiftedX >= 0 && shiftedY >= 0 && shiftedX < 104 && shiftedY < 104) {
								groundItems[plane][x][y] = groundItems[plane][shiftedX][shiftedY];
							} else {
								groundItems[plane][x][y] = null;
							}
						}
					}
				}

				for (ObjectSpawn object = (ObjectSpawn) spawns.getFront(); object != null; object = (ObjectSpawn) spawns
						.getNext()) {
					object.x -= dx;
					object.y -= dy;
					if (object.x < 0 || object.y < 0 || object.x >= 104 || object.y >= 104) {
						object.unlink();
					}
				}

				if (destinationX != 0) {
					destinationX -= dx;
					destinationY -= dy;
				}
				oriented = false;
				opcode = -1;
				return true;
			}
			if (opcode == 208) {
				int id = incomingBuffer.readLEShort();
				if (id >= 0) {
					resetAnimation(id);
				}
				currentStatusInterface = id;
				opcode = -1;
				return true;
			}
			if (opcode == 99) {
				minimapState = incomingBuffer.readUByte();
				opcode = -1;
				return true;
			}
			if (opcode == 75) {
				int npc = incomingBuffer.readLEUShortA();
				int id = incomingBuffer.readLEUShortA();
				Widget.widgets[id].mediaType = 2;
				Widget.widgets[id].media = npc;
				opcode = -1;
				return true;
			}
			if (opcode == 114) {
				systemUpdateTime = incomingBuffer.readLEUShort() * 30;
				opcode = -1;
				return true;
			}
			if (opcode == 60) {
				localY = incomingBuffer.readUByte();
				localX = incomingBuffer.readNegUByte();
				while (incomingBuffer.position < packetSize) {
					int id = incomingBuffer.readUByte();
					parseSectorPackets(incomingBuffer, id);
				}
				opcode = -1;
				return true;
			}
			if (opcode == 35) {
				int parameter = incomingBuffer.readUByte();
				int jitter = incomingBuffer.readUByte();
				int amplitude = incomingBuffer.readUByte();
				int frequency = incomingBuffer.readUByte();
				aBooleanArray876[parameter] = true;
				anIntArray873[parameter] = jitter;
				anIntArray1203[parameter] = amplitude;
				anIntArray928[parameter] = frequency;
				anIntArray1030[parameter] = 0;
				opcode = -1;
				return true;
			}
			if (opcode == 174) {
				int id = incomingBuffer.readUShort();
				int loop = incomingBuffer.readUByte();
				int delay = incomingBuffer.readUShort();
				if (aBoolean848 && !lowMemory && trackCount < 50) {
					tracks[trackCount] = id;
					trackLoops[trackCount] = loop;
					trackDelays[trackCount] = delay + Track.delays[id];
					trackCount++;
				}
				opcode = -1;
				return true;
			}
			if (opcode == 104) {
				int slot = incomingBuffer.readNegUByte();
				int primary = incomingBuffer.readUByteA();
				String message = incomingBuffer.readString();
				if (slot >= 1 && slot <= 5) {
					if (message.equalsIgnoreCase("null")) {
						message = null;
					}
					aStringArray1127[slot - 1] = message;
					aBooleanArray1128[slot - 1] = primary == 0;
				}
				opcode = -1;
				return true;
			}
			if (opcode == 78) {
				destinationX = 0;
				opcode = -1;
				return true;
			}
			if (opcode == 253) {
				String message = incomingBuffer.readString();
				if (message.endsWith(":tradereq:")) {
					String name = message.substring(0, message.indexOf(":"));
					long encodedName = StringUtils.encodeBase37(name);
					boolean ignored = false;
					for (int i = 0; i < ignoredCount; i++) {
						if (ignoredPlayers[i] != encodedName) {
							continue;
						}
						ignored = true;
						break;
					}

					if (!ignored && onTutorialIsland == 0) {
						addChatMessage(4, "wishes to trade with you.", name);
					}
				} else if (message.endsWith(":duelreq:")) {
					String name = message.substring(0, message.indexOf(":"));
					long encodedName = StringUtils.encodeBase37(name);
					boolean ignored = false;
					for (int i = 0; i < ignoredCount; i++) {
						if (ignoredPlayers[i] != encodedName) {
							continue;
						}
						ignored = true;
						break;
					}

					if (!ignored && onTutorialIsland == 0) {
						addChatMessage(8, "wishes to duel with you.", name);
					}
				} else if (message.endsWith(":chalreq:")) {
					String name = message.substring(0, message.indexOf(":"));
					long encodedName = StringUtils.encodeBase37(name);
					boolean ignored = false;
					for (int i = 0; i < ignoredCount; i++) {
						if (ignoredPlayers[i] != encodedName) {
							continue;
						}
						ignored = true;
						break;
					}

					if (!ignored && onTutorialIsland == 0) {
						String chatMessage = message.substring(message.indexOf(":") + 1, message.length() - 9);
						addChatMessage(8, chatMessage, name);
					}
				} else {
					addChatMessage(0, message, "");
				}
				opcode = -1;
				return true;
			}
			if (opcode == 1) {
				for (int index = 0; index < players.length; index++) {
					if (players[index] != null) {
						players[index].emoteAnimation = -1;
					}
				}

				for (int index = 0; index < npcs.length; index++) {
					if (npcs[index] != null) {
						npcs[index].emoteAnimation = -1;
					}
				}

				opcode = -1;
				return true;
			}
			if (opcode == 50) {
				long encodedName = incomingBuffer.readLong();
				int world = incomingBuffer.readUByte();
				String name = StringUtils.format(StringUtils.decodeBase37(encodedName));
				for (int player = 0; player < friendCount; player++) {
					if (encodedName != friends[player]) {
						continue;
					}
					if (friendWorlds[player] != world) {
						friendWorlds[player] = world;
						redrawTabArea = true;
						if (world > 0) {
							addChatMessage(5, name + " has logged in.", "");
						}
						if (world == 0) {
							addChatMessage(5, name + " has logged out.", "");
						}
					}
					name = null;
					break;
				}

				if (name != null && friendCount < 200) {
					friends[friendCount] = encodedName;
					friendUsernames[friendCount] = name;
					friendWorlds[friendCount] = world;
					friendCount++;
					redrawTabArea = true;
				}
				for (boolean flag6 = false; !flag6;) {
					flag6 = true;
					for (int i = 0; i < friendCount - 1; i++) {
						if (friendWorlds[i] != nodeId && friendWorlds[i + 1] == nodeId || friendWorlds[i] == 0
								&& friendWorlds[i + 1] != 0) {
							int j31 = friendWorlds[i];
							friendWorlds[i] = friendWorlds[i + 1];
							friendWorlds[i + 1] = j31;
							String s10 = friendUsernames[i];
							friendUsernames[i] = friendUsernames[i + 1];
							friendUsernames[i + 1] = s10;
							long l32 = friends[i];
							friends[i] = friends[i + 1];
							friends[i + 1] = l32;
							redrawTabArea = true;
							flag6 = false;
						}
					}
				}

				opcode = -1;
				return true;
			}
			if (opcode == 110) {
				if (tabId == 12) {
					redrawTabArea = true;
				}
				runEnergy = incomingBuffer.readUByte();
				opcode = -1;
				return true;
			}
			if (opcode == 254) {
				headIconDrawType = incomingBuffer.readUByte();
				if (headIconDrawType == 1) {
					anInt1222 = incomingBuffer.readUShort();
				}
				if (headIconDrawType >= 2 && headIconDrawType <= 6) {
					if (headIconDrawType == 2) {
						anInt937 = 64;
						anInt938 = 64;
					}
					if (headIconDrawType == 3) {
						anInt937 = 0;
						anInt938 = 64;
					}
					if (headIconDrawType == 4) {
						anInt937 = 128;
						anInt938 = 64;
					}
					if (headIconDrawType == 5) {
						anInt937 = 64;
						anInt938 = 0;
					}
					if (headIconDrawType == 6) {
						anInt937 = 64;
						anInt938 = 128;
					}
					headIconDrawType = 2;
					anInt934 = incomingBuffer.readUShort();
					anInt935 = incomingBuffer.readUShort();
					anInt936 = incomingBuffer.readUByte();
				}
				if (headIconDrawType == 10) {
					lastInteractedWithPlayer = incomingBuffer.readUShort();
				}
				opcode = -1;
				return true;
			}
			if (opcode == 248) {
				int id = incomingBuffer.readUShortA();
				int overlay = incomingBuffer.readUShort();
				if (backDialogueId != -1) {
					backDialogueId = -1;
					redrawChatbox = true;
				}
				if (inputDialogueState != 0) {
					inputDialogueState = 0;
					redrawChatbox = true;
				}
				openInterfaceId = id;
				inventoryOverlayInterfaceId = overlay;
				redrawTabArea = true;
				aBoolean1103 = true;
				aBoolean1149 = false;
				opcode = -1;
				return true;
			}
			if (opcode == 79) {
				int id = incomingBuffer.readLEUShort();
				int scrollPosition = incomingBuffer.readUShortA();
				Widget widget = Widget.widgets[id];
				if (widget != null && widget.type == 0) {
					if (scrollPosition < 0) {
						scrollPosition = 0;
					}
					if (scrollPosition > widget.scrollLimit - widget.height) {
						scrollPosition = widget.scrollLimit - widget.height;
					}
					widget.scrollPosition = scrollPosition;
				}
				opcode = -1;
				return true;
			}
			if (opcode == 68) {
				for (int i = 0; i < settings.length; i++) {
					if (settings[i] != anIntArray1045[i]) {
						settings[i] = anIntArray1045[i];
						method33(i);
						redrawTabArea = true;
					}
				}

				opcode = -1;
				return true;
			}
			if (opcode == 196) {
				long name = incomingBuffer.readLong();
				int messageId = incomingBuffer.readInt();
				int privilege = incomingBuffer.readUByte();
				boolean invalid = false;
				for (int i = 0; i < 100; i++) {
					if (privateMessageIds[i] != messageId) {
						continue;
					}
					invalid = true;
					break;
				}

				if (privilege <= 1) {
					for (int i = 0; i < ignoredCount; i++) {
						if (ignoredPlayers[i] != name) {
							continue;
						}
						invalid = true;
						break;
					}

				}
				if (!invalid && onTutorialIsland == 0) {
					try {
						privateMessageIds[privateMessageCount] = messageId;
						privateMessageCount = (privateMessageCount + 1) % 100;
						String message = ChatMessageCodec.decode(incomingBuffer, packetSize - 13);
						if (privilege != 3) {
							message = MessageCensor.apply(message);
						}
						if (privilege == 2 || privilege == 3) {
							addChatMessage(7, message, "@cr2@" + StringUtils.format(StringUtils.decodeBase37(name)));
						} else if (privilege == 1) {
							addChatMessage(7, message, "@cr1@" + StringUtils.format(StringUtils.decodeBase37(name)));
						} else {
							addChatMessage(3, message, StringUtils.format(StringUtils.decodeBase37(name)));
						}
					} catch (Exception exception1) {
						SignLink.reportError("cde1");
					}
				}
				opcode = -1;
				return true;
			}
			if (opcode == 85) {
				localY = incomingBuffer.readNegUByte();
				localX = incomingBuffer.readNegUByte();
				opcode = -1;
				return true;
			}
			if (opcode == 24) {
				flashingSidebarId = incomingBuffer.readUByteS();
				if (flashingSidebarId == tabId) {
					if (flashingSidebarId == 3) {
						tabId = 1;
					} else {
						tabId = 3;
					}
					redrawTabArea = true;
				}
				opcode = -1;
				return true;
			}
			if (opcode == 246) {
				int widget = incomingBuffer.readLEUShort();
				int scale = incomingBuffer.readUShort();
				int itemid = incomingBuffer.readUShort();
				if (itemid == 65535) {
					Widget.widgets[widget].mediaType = 0;
					opcode = -1;
					return true;
				}
				ItemDefinition definition = ItemDefinition.lookup(itemid);
				Widget.widgets[widget].mediaType = 4;
				Widget.widgets[widget].media = itemid;
				Widget.widgets[widget].spritePitch = definition.spritePitch;
				Widget.widgets[widget].spriteRoll = definition.spriteCameraRoll;
				Widget.widgets[widget].spriteScale = definition.spriteScale * 100 / scale;
				opcode = -1;
				return true;
			}
			if (opcode == 171) {
				boolean flag1 = incomingBuffer.readUByte() == 1;
				int id = incomingBuffer.readUShort();
				Widget.widgets[id].aBoolean266 = flag1;
				opcode = -1;
				return true;
			}
			if (opcode == 142) {
				int id = incomingBuffer.readLEUShort();
				resetAnimation(id);
				if (backDialogueId != -1) {
					backDialogueId = -1;
					redrawChatbox = true;
				}
				if (inputDialogueState != 0) {
					inputDialogueState = 0;
					redrawChatbox = true;
				}
				inventoryOverlayInterfaceId = id;
				redrawTabArea = true;
				aBoolean1103 = true;
				openInterfaceId = -1;
				aBoolean1149 = false;
				opcode = -1;
				return true;
			}
			if (opcode == 126) {
				String text = incomingBuffer.readString();
				int id = incomingBuffer.readUShortA();
				Widget.widgets[id].hiddenText = text;
				if (Widget.widgets[id].parent == inventoryTabIds[tabId]) {
					redrawTabArea = true;
				}
				opcode = -1;
				return true;
			}
			if (opcode == 206) {
				publicChatMode = incomingBuffer.readUByte();
				privateChatMode = incomingBuffer.readUByte();
				tradeChatMode = incomingBuffer.readUByte();
				aBoolean1233 = true;
				redrawChatbox = true;
				opcode = -1;
				return true;
			}
			if (opcode == 240) {
				if (tabId == 12) {
					redrawTabArea = true;
				}
				weight = incomingBuffer.readShort();
				opcode = -1;
				return true;
			}
			if (opcode == 8) {
				int id = incomingBuffer.readLEUShortA();
				int model = incomingBuffer.readUShort();
				Widget.widgets[id].mediaType = 1;
				Widget.widgets[id].media = model;
				opcode = -1;
				return true;
			}
			if (opcode == 122) {
				int id = incomingBuffer.readLEUShortA();
				int colour = incomingBuffer.readLEUShortA();
				int r = colour >> 10 & 0x1f;
				int g = colour >> 5 & 0x1f;
				int b = colour & 0x1f;
				Widget.widgets[id].colour = (r << 19) + (g << 11) + (b << 3);
				opcode = -1;
				return true;
			}
			if (opcode == 53) {
				redrawTabArea = true;
				int widgetId = incomingBuffer.readUShort();
				Widget widget = Widget.widgets[widgetId];
				int itemCount = incomingBuffer.readUShort();
				for (int slot = 0; slot < itemCount; slot++) {
					int amount = incomingBuffer.readUByte();
					if (amount == 255) {
						amount = incomingBuffer.readIMEInt();
					}
					widget.inventoryIds[slot] = incomingBuffer.readLEUShortA();
					widget.inventoryAmounts[slot] = amount;
				}

				for (int slot = itemCount; slot < widget.inventoryIds.length; slot++) {
					widget.inventoryIds[slot] = 0;
					widget.inventoryAmounts[slot] = 0;
				}
				opcode = -1;
				return true;
			}
			if (opcode == 230) {
				int scale = incomingBuffer.readUShortA();
				int id = incomingBuffer.readUShort();
				int pitch = incomingBuffer.readUShort();
				int roll = incomingBuffer.readLEUShortA();
				Widget.widgets[id].spritePitch = pitch;
				Widget.widgets[id].spriteRoll = roll;
				Widget.widgets[id].spriteScale = scale;
				opcode = -1;
				return true;
			}
			if (opcode == 221) {
				friendServerStatus = incomingBuffer.readUByte();
				redrawTabArea = true;
				opcode = -1;
				return true;
			}
			if (opcode == 177) {
				oriented = true;
				anInt995 = incomingBuffer.readUByte(); // local x
				anInt996 = incomingBuffer.readUByte(); // local y
				anInt997 = incomingBuffer.readUShort(); // height offset
				anInt998 = incomingBuffer.readUByte();
				anInt999 = incomingBuffer.readUByte(); // some sort of magnitude?

				if (anInt999 >= 100) {
					int x = anInt995 * 128 + 64;
					int y = anInt996 * 128 + 64;
					int height = method42(x, y, plane) - anInt997;
					int dx = x - anInt858;
					int dz = height - anInt859;
					int dy = y - anInt860;
					int r = (int) Math.sqrt(dx * dx + dy * dy);

					anInt861 = (int) (Math.atan2(dz, r) * 325.94900000000001D) & 0x7ff; // some angle
					anInt862 = (int) (Math.atan2(dx, dy) * -325.94900000000001D) & 0x7ff; // some angle

					if (anInt861 < 128) {
						anInt861 = 128;
					} else if (anInt861 > 383) {
						anInt861 = 383;
					}
				}

				opcode = -1;
				return true;
			}
			if (opcode == 249) {
				member = incomingBuffer.readUByteA();
				localPlayerIndex = incomingBuffer.readLEUShortA();
				opcode = -1;
				return true;
			}
			if (opcode == 65) {
				synchronizeNpcs(incomingBuffer, packetSize);
				opcode = -1;
				return true;
			}
			if (opcode == 27) {
				messagePromptRaised = false;
				inputDialogueState = 1;
				textInput = "";
				redrawChatbox = true;
				opcode = -1;
				return true;
			}
			if (opcode == 187) {
				messagePromptRaised = false;
				inputDialogueState = 2;
				textInput = "";
				redrawChatbox = true;
				opcode = -1;
				return true;
			}
			if (opcode == 97) {
				int id = incomingBuffer.readUShort();
				resetAnimation(id);
				if (inventoryOverlayInterfaceId != -1) {
					inventoryOverlayInterfaceId = -1;
					redrawTabArea = true;
					aBoolean1103 = true;
				}
				if (backDialogueId != -1) {
					backDialogueId = -1;
					redrawChatbox = true;
				}
				if (inputDialogueState != 0) {
					inputDialogueState = 0;
					redrawChatbox = true;
				}
				openInterfaceId = id;
				aBoolean1149 = false;
				opcode = -1;
				return true;
			}
			if (opcode == 218) {
				dialogueId = incomingBuffer.readLEShortA();
				redrawChatbox = true;
				opcode = -1;
				return true;
			}
			if (opcode == 87) {
				int id = incomingBuffer.readLEUShort();
				int value = incomingBuffer.readMEInt();
				anIntArray1045[id] = value;
				if (settings[id] != value) {
					settings[id] = value;
					method33(id);
					redrawTabArea = true;
					if (dialogueId != -1) {
						redrawChatbox = true;
					}
				}
				opcode = -1;
				return true;
			}
			if (opcode == 36) {
				int id = incomingBuffer.readLEUShort();
				byte value = incomingBuffer.readByte();
				anIntArray1045[id] = value;
				if (settings[id] != value) {
					settings[id] = value;
					method33(id);
					redrawTabArea = true;
					if (dialogueId != -1) {
						redrawChatbox = true;
					}
				}
				opcode = -1;
				return true;
			}
			if (opcode == 61) {
				multicombat = incomingBuffer.readUByte();
				opcode = -1;
				return true;
			}
			if (opcode == 200) {
				int id = incomingBuffer.readUShort();
				int animation = incomingBuffer.readShort();
				Widget widget = Widget.widgets[id];
				widget.mediaAnimationId = animation;
				if (animation == -1) {
					widget.displayedFrameCount = 0;
					widget.lastFrameTime = 0;
				}
				opcode = -1;
				return true;
			}
			if (opcode == 219) {
				if (inventoryOverlayInterfaceId != -1) {
					inventoryOverlayInterfaceId = -1;
					redrawTabArea = true;
					aBoolean1103 = true;
				}
				if (backDialogueId != -1) {
					backDialogueId = -1;
					redrawChatbox = true;
				}
				if (inputDialogueState != 0) {
					inputDialogueState = 0;
					redrawChatbox = true;
				}
				openInterfaceId = -1;
				aBoolean1149 = false;
				opcode = -1;
				return true;
			}
			if (opcode == 34) {
				redrawTabArea = true;
				int widgetId = incomingBuffer.readUShort();
				Widget widget = Widget.widgets[widgetId];
				while (incomingBuffer.position < packetSize) {
					int slot = incomingBuffer.readSmart();
					int id = incomingBuffer.readUShort();
					int amount = incomingBuffer.readUByte();
					if (amount == 255) {
						amount = incomingBuffer.readInt();
					}
					if (slot >= 0 && slot < widget.inventoryIds.length) {
						widget.inventoryIds[slot] = id;
						widget.inventoryAmounts[slot] = amount;
					}
				}
				opcode = -1;
				return true;
			}
			if (opcode == 105 || opcode == 84 || opcode == 147 || opcode == 215 || opcode == 4 || opcode == 117 || opcode == 156
					|| opcode == 44 || opcode == 160 || opcode == 101 || opcode == 151) {
				parseSectorPackets(incomingBuffer, opcode);
				opcode = -1;
				return true;
			}
			if (opcode == 106) { // tab interface
				tabId = incomingBuffer.readNegUByte();
				redrawTabArea = true;
				aBoolean1103 = true;
				opcode = -1;
				return true;
			}
			if (opcode == 164) {
				int id = incomingBuffer.readLEUShort();
				resetAnimation(id);
				if (inventoryOverlayInterfaceId != -1) {
					inventoryOverlayInterfaceId = -1;
					redrawTabArea = true;
					aBoolean1103 = true;
				}
				backDialogueId = id;
				redrawChatbox = true;
				openInterfaceId = -1;
				aBoolean1149 = false;
				opcode = -1;
				return true;
			}
			System.out.println("T1 - unrecognised packet error - opcode: " + opcode + ", packet size:" + packetSize
					+ ", previous opcode: " + secondLastOpcode + ", third last opcode: " + thirdLastOpcode);

			// com.jagex.SignLink.reportError("T1 - " + opcode + "," + packetSize + " - " + anInt842 + "," + anInt843);
			reset();
		} catch (IOException ex) {
			attemptReconnection();
		} catch (Exception exception) {
			System.out.println("T2 - exception - opcode " + opcode + ", previous opcode: " + secondLastOpcode
					+ ", third last opcode: " + thirdLastOpcode + "\n" + exception.getMessage() + "\n"
					+ exception.getStackTrace());

			String s2 = "T2 - " + opcode + "," + secondLastOpcode + "," + thirdLastOpcode + " - " + packetSize + ","
					+ (regionBaseX + localPlayer.pathX[0]) + "," + (regionBaseY + localPlayer.pathY[0]) + " - ";
			for (int j15 = 0; j15 < packetSize && j15 < 50; j15++) {
				s2 = s2 + incomingBuffer.payload[j15] + ",";
			}

			// com.jagex.SignLink.reportError(s2);
			reset();
		}
		return true;
	}

	public final void parseSectorPackets(Buffer buffer, int opcode) {
		if (opcode == 84) {
			int offset = buffer.readUByte(); // offset from
			int locX = localX + (offset >> 4 & 7);
			int locY = localY + (offset & 7);
			int id = buffer.readUShort();
			int previousStackSize = buffer.readUShort();
			int newStackSize = buffer.readUShort();
			if (locX >= 0 && locY >= 0 && locX < 104 && locY < 104) {
				Deque deque = groundItems[plane][locX][locY];
				if (deque != null) {
					for (Item item = (Item) deque.getFront(); item != null; item = (Item) deque.getNext()) {
						if (item.id != (id & 0x7fff) || item.amount != previousStackSize) {
							continue;
						}
						item.amount = newStackSize;
						break;
					}

					processGroundItems(locX, locY); // update the tile
				}
			}
			return;
		}
		if (opcode == 105) {
			int positionOffset = buffer.readUByte();
			int x = localX + (positionOffset >> 4 & 7);
			int y = localY + (positionOffset & 7);
			int id = buffer.readUShort();
			int data = buffer.readUByte();
			int positionDelta = data >> 4 & 0xf;
			int loop = data & 7;
			if (localPlayer.pathX[0] >= x - positionDelta && localPlayer.pathX[0] <= x + positionDelta
					&& localPlayer.pathY[0] >= y - positionDelta && localPlayer.pathY[0] <= y + positionDelta && aBoolean848
					&& !lowMemory && trackCount < 50) {
				tracks[trackCount] = id;
				trackLoops[trackCount] = loop;
				trackDelays[trackCount] = Track.delays[id];
				trackCount++;
			}
		}
		if (opcode == 215) {
			int id = buffer.readUShortA();
			int positionOffset = buffer.readUByteS();
			int x = localX + (positionOffset >> 4 & 7);
			int y = localY + (positionOffset & 7);
			int index = buffer.readUShortA();
			int amount = buffer.readUShort();
			if (x >= 0 && y >= 0 && x < 104 && y < 104 && index != localPlayerIndex) {
				Item item = new Item();
				item.id = id;
				item.amount = amount;
				if (groundItems[plane][x][y] == null) {
					groundItems[plane][x][y] = new Deque();
				}
				groundItems[plane][x][y].pushBack(item);
				processGroundItems(x, y);
			}
			return;
		}
		if (opcode == 156) {
			int positionOffset = buffer.readUByteA();
			int x = localX + (positionOffset >> 4 & 7);
			int y = localY + (positionOffset & 7);
			int id = buffer.readUShort();
			if (x >= 0 && y >= 0 && x < 104 && y < 104) {
				Deque items = groundItems[plane][x][y];
				if (items != null) {
					for (Item item = (Item) items.getFront(); item != null; item = (Item) items.getNext()) {
						if (item.id != (id & 0x7fff)) {
							continue;
						}
						item.unlink();
						break;
					}

					if (items.getFront() == null) {
						groundItems[plane][x][y] = null;
					}
					processGroundItems(x, y);
				}
			}
			return;
		}
		if (opcode == 160) {
			int positionOffset = buffer.readUByteS();
			int x = localX + (positionOffset >> 4 & 7);
			int y = localY + (positionOffset & 7);
			int info = buffer.readUByteS();
			int objectType = info >> 2;
			int orientation = info & 3;
			int type = anIntArray1177[objectType];
			int animation = buffer.readUShortA();
			if (x >= 0 && y >= 0 && x < 103 && y < 103) {
				int j18 = anIntArrayArrayArray1214[plane][x][y];
				int i19 = anIntArrayArrayArray1214[plane][x + 1][y];
				int l19 = anIntArrayArrayArray1214[plane][x + 1][y + 1];
				int k20 = anIntArrayArrayArray1214[plane][x][y + 1];

				if (type == 0) {
					Wall wall = scene.getTileWall(x, y, plane);
					if (wall != null) {
						int k21 = wall.key >> 14 & 0x7fff;
						if (objectType == 2) {
							wall.aClass30_Sub2_Sub4_278 = new GameObject(k21, 4 + orientation, 2, i19, l19, j18, k20, animation,
									false);
							wall.aClass30_Sub2_Sub4_279 = new GameObject(k21, orientation + 1 & 3, 2, i19, l19, j18, k20,
									animation, false);
						} else {
							wall.aClass30_Sub2_Sub4_278 = new GameObject(k21, orientation, objectType, i19, l19, j18, k20,
									animation, false);
						}
					}
				} else if (type == 1) {
					WallDecoration decor = scene.getTileWallDecoration(x, y, plane);
					if (decor != null) {
						decor.renderable = new GameObject(decor.key >> 14 & 0x7fff, 0, 4, i19, l19, j18, k20, animation, false);
					}
				} else if (type == 2) {
					InteractableObject object = scene.method298(x, y, plane);
					if (objectType == 11) {
						objectType = 10;
					}
					if (object != null) {
						object.renderable = new GameObject(object.key >> 14 & 0x7fff, orientation, objectType, i19, l19, j18,
								k20, animation, false);
					}
				} else if (type == 3) {
					FloorDecoration decor = scene.getTileFloorDecoration(x, y, plane);
					if (decor != null) {
						decor.renderable = new GameObject(decor.key >> 14 & 0x7fff, orientation, 22, i19, l19, j18, k20,
								animation, false);
					}
				}
			}
			return;
		}
		if (opcode == 147) {
			int positionOffset = buffer.readUByteS();
			int x = localX + (positionOffset >> 4 & 7);
			int y = localY + (positionOffset & 7);
			int index = buffer.readUShort();
			byte byte0 = buffer.readByteS();
			int delay = buffer.readLEUShort();
			byte byte1 = buffer.readNegByte();
			int length = buffer.readUShort(); // time in ticks until the player transforms again
			int k18 = buffer.readUByteS();
			int position = k18 >> 2;
			int orientation = k18 & 3;
			int type = anIntArray1177[position];
			byte byte2 = buffer.readByte();
			int id = buffer.readUShort();
			byte byte3 = buffer.readNegByte();
			Player player = (index == localPlayerIndex) ? localPlayer : players[index];

			if (player != null) {
				ObjectDefinition definition = ObjectDefinition.lookup(id);
				int i22 = anIntArrayArrayArray1214[plane][x][y];
				int j22 = anIntArrayArrayArray1214[plane][x + 1][y];
				int k22 = anIntArrayArrayArray1214[plane][x + 1][y + 1];
				int l22 = anIntArrayArrayArray1214[plane][x][y + 1];
				Model model = definition.modelAt(position, orientation, i22, j22, k22, l22, -1);
				if (model != null) {
					method130(-1, x, y, plane, type, 0, length + 1, 0, delay + 1);
					player.objectAppearanceStartTick = delay + tick;
					player.objectAppearanceEndTick = length + tick;
					player.objectModel = model;
					int dx = definition.width;
					int dy = definition.length;
					if (orientation == 1 || orientation == 3) {
						dx = definition.length;
						dy = definition.width;
					}

					player.anInt1711 = x * 128 + dx * 64;
					player.anInt1713 = y * 128 + dy * 64;
					player.anInt1712 = method42(player.anInt1711, player.anInt1713, plane);
					if (byte2 > byte0) {
						byte byte4 = byte2;
						byte2 = byte0;
						byte0 = byte4;
					}
					if (byte3 > byte1) {
						byte byte5 = byte3;
						byte3 = byte1;
						byte1 = byte5;
					}
					player.anInt1719 = x + byte2;
					player.anInt1721 = x + byte0;
					player.anInt1720 = y + byte3;
					player.anInt1722 = y + byte1;
				}
			}
		}
		if (opcode == 151) { // add game object
			int positionOffset = buffer.readUByteA();
			int x = localX + (positionOffset >> 4 & 7);
			int y = localY + (positionOffset & 7);
			int id = buffer.readLEUShort();
			int info = buffer.readUByteS();
			int typeIndex = info >> 2;
			int orientation = info & 3;
			int type = anIntArray1177[typeIndex];
			if (x >= 0 && y >= 0 && x < 104 && y < 104) {
				method130(id, x, y, plane, type, orientation, -1, typeIndex, 0);
			}
			return;
		}
		if (opcode == 4) {
			int positionOffset = buffer.readUByte();
			int x = localX + (positionOffset >> 4 & 7);
			int y = localY + (positionOffset & 7);
			int graphic = buffer.readUShort();
			int renderOffset = buffer.readUByte();
			int delay = buffer.readUShort();
			if (x >= 0 && y >= 0 && x < 104 && y < 104) {
				x = x * 128 + 64;
				y = y * 128 + 64;
				AnimableObject object = new AnimableObject(x, y, plane, method42(x, y, plane) - renderOffset, graphic, delay,
						tick);
				incompleteAnimables.pushBack(object);
			}
			return;
		}
		if (opcode == 44) {
			int id = buffer.readLEUShortA();
			int amount = buffer.readUShort();
			int positionOffset = buffer.readUByte();
			int x = localX + (positionOffset >> 4 & 7);
			int y = localY + (positionOffset & 7);
			if (x >= 0 && y >= 0 && x < 104 && y < 104) {
				Item item = new Item();
				item.id = id;
				item.amount = amount;
				if (groundItems[plane][x][y] == null) {
					groundItems[plane][x][y] = new Deque();
				}
				groundItems[plane][x][y].pushBack(item);
				processGroundItems(x, y);
			}
			return;
		}
		if (opcode == 101) {
			int info = buffer.readNegUByte();
			int type = info >> 2;
			int orientation = info & 3;
			int i11 = anIntArray1177[type];
			int positionOffset = buffer.readUByte();
			int x = localX + (positionOffset >> 4 & 7);
			int y = localY + (positionOffset & 7);
			if (x >= 0 && y >= 0 && x < 104 && y < 104) {
				method130(-1, x, y, plane, i11, orientation, -1, type, 0);
			}
			return;
		}
		if (opcode == 117) {
			int positionOffset = buffer.readUByte();
			int sourceX = localX + (positionOffset >> 4 & 7);
			int sourceY = localY + (positionOffset & 7);
			int destinationX = sourceX + buffer.readByte();
			int destinationY = sourceY + buffer.readByte();
			int target = buffer.readShort();
			int graphic = buffer.readUShort();
			int sourceElevationOffset = buffer.readUByte() * 4;
			int destinationElevation = buffer.readUByte() * 4;
			int ticksToEnd = buffer.readUShort();
			int ticksToStart = buffer.readUShort();
			int elevationPitch = buffer.readUByte();
			int leapScale = buffer.readUByte();
			if (sourceX >= 0 && sourceY >= 0 && sourceX < 104 && sourceY < 104 && destinationX >= 0 && destinationY >= 0
					&& destinationX < 104 && destinationY < 104 && graphic != 65535) {
				sourceX = sourceX * 128 + 64;
				sourceY = sourceY * 128 + 64;
				destinationX = destinationX * 128 + 64;
				destinationY = destinationY * 128 + 64;
				Projectile projectile = new Projectile(sourceX, sourceY, method42(sourceX, sourceY, plane)
						- sourceElevationOffset, destinationElevation, elevationPitch, ticksToStart + tick, ticksToEnd + tick,
						leapScale, plane, target, graphic);
				projectile.target(destinationX, destinationY, method42(destinationX, destinationY, plane) - destinationElevation,
						ticksToEnd + tick);
				projectiles.pushBack(projectile);
			}
		}
	}

	public final void processAnimableObjects() {
		AnimableObject object = (AnimableObject) incompleteAnimables.getFront();

		for (; object != null; object = (AnimableObject) incompleteAnimables.getNext()) {
			if (object.z != plane || object.transformationCompleted) {
				object.unlink();
			} else if (tick >= object.cycle) {
				object.nextAnimationStep(tickDelta);
				if (object.transformationCompleted) {
					object.unlink();
				} else {
					scene.addEntity(object.x, object.y, object.z, object, 0, -1, object.renderHeight, 60, false);
				}
			}
		}
	}

	public final void processGroundItems(int x, int y) {
		Deque deque = groundItems[plane][x][y];
		if (deque == null) {
			scene.clearGroundItem(x, y, plane);
			return;
		}
		int maxValue = 0xfa0a1f01;
		Item mostValuable = null;
		for (Item item = (Item) deque.getFront(); item != null; item = (Item) deque.getNext()) {
			ItemDefinition definition = ItemDefinition.lookup(item.id);
			int value = definition.value;
			if (definition.stackable) {
				value *= item.amount + 1;
			}
			if (value > maxValue) {
				maxValue = value;
				mostValuable = item;
			}
		}

		deque.pushFront(mostValuable);
		Item first = null;
		Item second = null;
		for (Item item = (Item) deque.getFront(); item != null; item = (Item) deque.getNext()) {
			if (item.id != mostValuable.id && first == null) {
				first = item;
			}
			if (item.id != mostValuable.id && item.id != first.id && second == null) {
				second = item;
			}
		}

		int key = x + (y << 7) + 0x60000000;
		scene.addGroundItem(x, y, plane, key, mostValuable, first, second, method42(x * 128 + 64, y * 128 + 64, plane));
	}

	public final void processLoadedResources() {
		do {
			Resource request;
			do {
				request = provider.next();
				if (request == null) {
					return;
				}

				if (request.type == 0) {
					Model.load(request.data, request.file);
					if ((provider.getModelAttributes(request.file) & 0x62) != 0) {
						redrawTabArea = true;

						if (backDialogueId != -1) {
							redrawChatbox = true;
						}
					}
				}

				if (request.type == 1 && request.data != null) {
					Frame.load(request.data);
				} else if (request.type == 2 && request.file == musicId && request.data != null) {
					midiSave(request.data, fadeMusic);
				}

				if (request.type == 3 && loadingStage == 1) {
					for (int i = 0; i < localRegionMapData.length; i++) {
						if (localRegionMapIds[i] == request.file) {
							localRegionMapData[i] = request.data;

							if (request.data == null) {
								localRegionMapIds[i] = -1;
							}

							break;
						}

						if (localRegionLandscapeIds[i] != request.file) {
							continue;
						}

						localRegionLandscapeData[i] = request.data;
						if (request.data == null) {
							localRegionLandscapeIds[i] = -1;
						}

						break;
					}

				}
			} while (request.type != 93 || !provider.landscapePresent(request.file));

			Region.load(new Buffer(request.data), provider);
		} while (true);
	}

	public final void processMovement(Mob mob) {
		if (mob.worldX < 128 || mob.worldY < 128 || mob.worldX >= 13184 || mob.worldY >= 13184) {
			mob.emoteAnimation = -1;
			mob.graphicId = -1;
			mob.startForceMovement = 0;
			mob.endForceMovement = 0;
			mob.worldX = mob.pathX[0] * 128 + mob.size * 64;
			mob.worldY = mob.pathY[0] * 128 + mob.size * 64;
			mob.resetPath();
		}
		if (mob == localPlayer && (mob.worldX < 1536 || mob.worldY < 1536 || mob.worldX >= 11776 || mob.worldY >= 11776)) {
			mob.emoteAnimation = -1;
			mob.graphicId = -1;
			mob.startForceMovement = 0;
			mob.endForceMovement = 0;
			mob.worldX = mob.pathX[0] * 128 + mob.size * 64;
			mob.worldY = mob.pathY[0] * 128 + mob.size * 64;
			mob.resetPath();
		}
		if (mob.startForceMovement > tick) {
			nextPreForcedStep(mob); // TODO two periods of motion instead of one??
		} else if (mob.endForceMovement >= tick) {
			nextForcedMovementStep(mob);
		} else {
			nextStep(mob);
		}
		method100(mob);
		method101(mob);
	}

	public final void processNpcAdditions(boolean flag) {
		for (int index = 0; index < npcCount; index++) {
			Npc npc = npcs[npcList[index]];
			int key = 0x20000000 + (npcList[index] << 14);
			if (npc == null || !npc.isVisible() || npc.definition.priorityRender != flag) {
				continue;
			}
			int viewportX = npc.worldX >> 7;
			int viewportY = npc.worldY >> 7;
			if (viewportX < 0 || viewportX >= 104 || viewportY < 0 || viewportY >= 104) {
				continue;
			}
			if (npc.size == 1 && (npc.worldX & 0x7f) == 64 && (npc.worldY & 0x7f) == 64) {
				if (anIntArrayArray929[viewportX][viewportY] == anInt1265) {
					continue;
				}
				anIntArrayArray929[viewportX][viewportY] = anInt1265;
			}
			if (!npc.definition.clickable) {
				key += 0x80000000;
			}
			scene.addEntity(npc.worldX, npc.worldY, plane, npc, npc.orientation, key, method42(npc.worldX, npc.worldY, plane),
					(npc.size - 1) * 64 + 60, npc.animationStretches);
		}
	}

	public final void processNpcMovement() {
		for (int j = 0; j < npcCount; j++) {
			int index = npcList[j];
			Npc npc = npcs[index];
			if (npc != null) {
				processMovement(npc);
			}
		}
	}

	public final void processPlayerAdditions(boolean isLocal) {
		if (localPlayer.worldX >> 7 == destinationX && localPlayer.worldY >> 7 == destinationY) {
			destinationX = 0;
		}

		int index = playerCount;
		if (isLocal) {
			index = 1;
		}

		for (int i = 0; i < index; i++) {
			Player player;
			int key;

			if (isLocal) {
				player = localPlayer;
				key = internalLocalPlayerIndex << 14;
			} else {
				player = players[playerList[i]];
				key = playerList[i] << 14;
			}

			if (player == null || !player.isVisible()) {
				continue;
			}

			player.aBoolean1699 = false;
			if ((lowMemory && playerCount > 50 || playerCount > 200) && !isLocal
					&& player.movementAnimation == player.idleAnimation) {
				player.aBoolean1699 = true;
			}

			int viewportX = player.worldX >> 7;
			int viewportY = player.worldY >> 7;
			if (viewportX < 0 || viewportX >= 104 || viewportY < 0 || viewportY >= 104) {
				continue;
			}

			if (player.objectModel != null && tick >= player.objectAppearanceStartTick && tick < player.objectAppearanceEndTick) {
				player.aBoolean1699 = false;
				player.anInt1709 = method42(player.worldX, player.worldY, plane);
				scene.method286(plane, player.worldY, player, player.orientation, player.anInt1722, player.worldX,
						player.anInt1709, player.anInt1719, player.anInt1721, key, player.anInt1720);
				continue;
			}

			if ((player.worldX & 0x7f) == 64 && (player.worldY & 0x7f) == 64) {
				if (anIntArrayArray929[viewportX][viewportY] == anInt1265) {
					continue;
				}
				anIntArrayArray929[viewportX][viewportY] = anInt1265;
			}

			player.anInt1709 = method42(player.worldX, player.worldY, plane);
			scene.addEntity(player.worldX, player.worldY, plane, player, player.orientation, key, player.anInt1709, 60,
					player.animationStretches);
		}
	}

	public final void processPlayerMovement() {
		for (int index = -1; index < playerCount; index++) {
			int playerIndex;

			if (index == -1) {
				playerIndex = internalLocalPlayerIndex;
			} else {
				playerIndex = playerList[index];
			}

			Player player = players[playerIndex];
			if (player != null) {
				processMovement(player);
			}
		}
	}

	public final void processProjectiles() {
		for (Projectile projectile = (Projectile) projectiles.getFront(); projectile != null; projectile = (Projectile) projectiles
				.getNext()) {
			if (projectile.plane != plane || tick > projectile.startTick) {
				projectile.unlink();
			} else if (tick >= projectile.endTick) {
				if (projectile.target > 0) {
					Npc npc = npcs[projectile.target - 1];
					if (npc != null && npc.worldX >= 0 && npc.worldX < 13312 && npc.worldY >= 0 && npc.worldY < 13312) {
						projectile.target(npc.worldX, npc.worldY, method42(npc.worldX, npc.worldY, projectile.plane)
								- projectile.destinationElevation, tick);
					}
				}
				if (projectile.target < 0) {
					int index = -projectile.target - 1;
					Player player;
					if (index == localPlayerIndex) {
						player = localPlayer;
					} else {
						player = players[index];
					}

					if (player != null && player.worldX >= 0 && player.worldX < 13312 && player.worldY >= 0
							&& player.worldY < 13312) {
						projectile.target(player.worldX, player.worldY, method42(player.worldX, player.worldY, projectile.plane)
								- projectile.destinationElevation, tick);
					}
				}
				projectile.update(tickDelta);
				scene.addEntity((int) projectile.x, (int) projectile.y, plane, projectile, projectile.yaw, -1,
						(int) projectile.z, 60, false);
			}
		}
	}

	public final boolean processWidgetAnimations(int widgetId, int tickDelta) {
		boolean redrawRequired = false;
		Widget widget = Widget.widgets[widgetId];
		for (int childId : widget.children) {
			if (childId == -1) {
				break;
			}

			Widget child = Widget.widgets[childId];
			if (child.type == 1) {
				redrawRequired |= processWidgetAnimations(child.id, tickDelta);
			}

			if (child.type == 6 && (child.mediaAnimationId != -1 || child.anInt258 != -1)) {
				boolean flag2 = method131(child);
				int animationId;

				if (flag2) {
					animationId = child.anInt258;
				} else {
					animationId = child.mediaAnimationId;
				}

				if (animationId != -1) {
					Animation animation = Animation.animations[animationId];
					for (child.lastFrameTime += tickDelta; child.lastFrameTime > animation.duration(child.displayedFrameCount);) {
						child.lastFrameTime -= animation.duration(child.displayedFrameCount) + 1;
						child.displayedFrameCount++;
						if (child.displayedFrameCount >= animation.frameCount) {
							child.displayedFrameCount -= animation.loopOffset;
							if (child.displayedFrameCount < 0 || child.displayedFrameCount >= animation.frameCount) {
								child.displayedFrameCount = 0;
							}
						}

						redrawRequired = true;
					}
				}
			}
		}

		return redrawRequired;
	}

	@Override
	public final void pulse() {
		if (gameAlreadyLoaded || error || unableToLoad) {
			return;
		}
		tick++;

		if (!loggedIn) {
			pulseLoginScreen();
		} else {
			pulseGame();
		}
		processLoadedResources();
	}

	public final void pulseGame() {
		if (systemUpdateTime > 1) {
			systemUpdateTime--;
		}
		if (anInt1011 > 0) {
			anInt1011--;
		}

		for (int i = 0; i < 5; i++) {
			if (!parseFrame()) {
				break;
			}
		}

		if (!loggedIn) {
			return;
		}

		synchronized (mouseCapturer.synchronizedObject) {
			if (flaggedAccount) {
				if (super.lastMetaModifier != 0 || mouseCapturer.capturedCoordinateCount >= 40) {
					outgoing.writeOpcode(45);
					outgoing.writeByte(0);
					int off = outgoing.position;
					int sent = 0;

					for (int coordinate = 0; coordinate < mouseCapturer.capturedCoordinateCount; coordinate++) {
						if (off - outgoing.position >= 240) {
							break;
						}

						sent++;
						int y = mouseCapturer.coordinatesY[coordinate];
						if (y < 0) {
							y = 0;
						} else if (y > 502) {
							y = 502;
						}

						int x = mouseCapturer.coordinatesX[coordinate];
						if (x < 0) {
							x = 0;
						} else if (x > 764) {
							x = 764;
						}

						int point = y * 765 + x;
						if (mouseCapturer.coordinatesY[coordinate] == -1 && mouseCapturer.coordinatesX[coordinate] == -1) {
							x = -1;
							y = -1;
							point = 0x7ffff;
						}

						if (x == lastMouseX && y == lastMouseY) {
							if (duplicateClickCount < 2047) {
								duplicateClickCount++;
							}
						} else {
							int dx = x - lastMouseX;
							lastMouseX = x;
							int dy = y - lastMouseY;
							lastMouseY = y;
							if (duplicateClickCount < 8 && dx >= -32 && dx <= 31 && dy >= -32 && dy <= 31) {
								dx += 32;
								dy += 32;
								outgoing.writeShort((duplicateClickCount << 12) + (dx << 6) + dy);
								duplicateClickCount = 0;
							} else if (duplicateClickCount < 8) {
								outgoing.writeTriByte(0x800000 + (duplicateClickCount << 19) + point);
								duplicateClickCount = 0;
							} else {
								outgoing.writeInt(0xc0000000 + (duplicateClickCount << 19) + point);
								duplicateClickCount = 0;
							}
						}
					}

					outgoing.writeSizeByte(outgoing.position - off);
					if (sent >= mouseCapturer.capturedCoordinateCount) {
						mouseCapturer.capturedCoordinateCount = 0;
					} else {
						mouseCapturer.capturedCoordinateCount -= sent;
						for (int i5 = 0; i5 < mouseCapturer.capturedCoordinateCount; i5++) {
							mouseCapturer.coordinatesX[i5] = mouseCapturer.coordinatesX[i5 + sent];
							mouseCapturer.coordinatesY[i5] = mouseCapturer.coordinatesY[i5 + sent];
						}

					}
				}
			} else {
				mouseCapturer.capturedCoordinateCount = 0;
			}
		}

		if (super.lastMetaModifier != 0) {
			int time = (int) ((super.lastMouseClick - aLong1220) / 50);
			if (time > 4095) {
				time = 4095;
			}

			aLong1220 = super.lastMouseClick;
			int y = super.lastClickY;
			if (y < 0) {
				y = 0;
			} else if (y > 502) {
				y = 502;
			}

			int x = super.lastClickX;
			if (x < 0) {
				x = 0;
			} else if (x > 764) {
				x = 764;
			}

			int key = y * 765 + x;
			int meta = 0;
			if (super.lastMetaModifier == 2) {
				meta = 1;
			}

			outgoing.writeOpcode(241);
			outgoing.writeInt((time << 20) + (meta << 19) + key);
		}

		if (anInt1016 > 0) {
			anInt1016--;
		}

		if (super.keyStatuses[1] == 1 || super.keyStatuses[2] == 1 || super.keyStatuses[3] == 1 || super.keyStatuses[4] == 1) {
			aBoolean1017 = true;
		}

		if (aBoolean1017 && anInt1016 <= 0) {
			anInt1016 = 20;
			aBoolean1017 = false;
			outgoing.writeOpcode(86);
			outgoing.writeShort(cameraRoll);
			outgoing.writeShortA(cameraYaw);
		}

		if (super.hasFocus && !wasFocused) {
			wasFocused = true;
			outgoing.writeOpcode(3);
			outgoing.writeByte(1);
		}

		if (!super.hasFocus && wasFocused) {
			wasFocused = false;
			outgoing.writeOpcode(3);
			outgoing.writeByte(0);
		}

		method53();
		method115();
		method90();
		timeoutCounter++;
		if (timeoutCounter > 750) {
			attemptReconnection();
		}

		processPlayerMovement();
		processNpcMovement();
		pulseMobChatText();
		tickDelta++;
		if (anInt917 != 0) {
			anInt916 += 20;
			if (anInt916 >= 400) {
				anInt917 = 0;
			}
		}

		if (anInt1246 != 0) {
			anInt1243++;
			if (anInt1243 >= 15) {
				if (anInt1246 == 2) {
					redrawTabArea = true;
				}
				if (anInt1246 == 3) {
					redrawChatbox = true;
				}
				anInt1246 = 0;
			}
		}
		if (anInt1086 != 0) {
			anInt989++;
			if (super.mouseEventX > anInt1087 + 5 || super.mouseEventX < anInt1087 - 5 || super.mouseEventY > anInt1088 + 5
					|| super.mouseEventY < anInt1088 - 5) {
				aBoolean1242 = true;
			}
			if (super.metaModifierHeld == 0) {
				if (anInt1086 == 2) {
					redrawTabArea = true;
				}
				if (anInt1086 == 3) {
					redrawChatbox = true;
				}
				anInt1086 = 0;
				if (aBoolean1242 && anInt989 >= 5) {
					anInt1067 = -1;
					method82(0);
					if (anInt1067 == anInt1084 && anInt1066 != anInt1085) {
						Widget widget = Widget.widgets[anInt1084];
						int j1 = 0;
						if (bankInsertMode == 1 && widget.anInt214 == 206) {
							j1 = 1;
						}
						if (widget.inventoryIds[anInt1066] <= 0) {
							j1 = 0;
						}
						if (widget.aBoolean235) {
							int l2 = anInt1085;
							int l3 = anInt1066;
							widget.inventoryIds[l3] = widget.inventoryIds[l2];
							widget.inventoryAmounts[l3] = widget.inventoryAmounts[l2];
							widget.inventoryIds[l2] = -1;
							widget.inventoryAmounts[l2] = 0;
						} else if (j1 == 1) {
							int i3 = anInt1085;
							for (int i4 = anInt1066; i3 != i4;) {
								if (i3 > i4) {
									widget.swapInventoryItems(i3, i3 - 1);
									i3--;
								} else if (i3 < i4) {
									widget.swapInventoryItems(i3, i3 + 1);
									i3++;
								}
							}

						} else {
							widget.swapInventoryItems(anInt1085, anInt1066);
						}
						outgoing.writeOpcode(214);
						outgoing.writeLEShortA(anInt1084); // interface
						outgoing.writeNegByte(j1); // inserting
						outgoing.writeLEShortA(anInt1085); // old
						outgoing.writeLEShort(anInt1066); // new
					}
				} else if ((mouseButtons == 1 || method17(menuActionRow - 1)) && menuActionRow > 2) {
					method116();
				} else if (menuActionRow > 0) {
					method69(menuActionRow - 1);
				}
				anInt1243 = 10;
				super.lastMetaModifier = 0;
			}
		}
		if (Scene.anInt470 != -1) {
			int k = Scene.anInt470;
			int k1 = Scene.anInt471;
			boolean flag = method85(0, 0, 0, 0, localPlayer.pathY[0], 0, 0, k1, localPlayer.pathX[0], true, k);
			Scene.anInt470 = -1;
			if (flag) {
				anInt914 = super.lastClickX;
				anInt915 = super.lastClickY;
				anInt917 = 1;
				anInt916 = 0;
			}
		}
		if (super.lastMetaModifier == 1 && clickToContinueString != null) {
			clickToContinueString = null;
			redrawChatbox = true;
			super.lastMetaModifier = 0;
		}
		method20();
		method92();
		method78();
		method32();
		if (super.metaModifierHeld == 1 || super.lastMetaModifier == 1) {
			anInt1213++;
		}
		if (loadingStage == 2) {
			method108();
		}
		if (loadingStage == 2 && oriented) {
			method39();
		}
		for (int i1 = 0; i1 < 5; i1++) {
			anIntArray1030[i1]++;
		}

		processInput();
		super.timeIdle++;
		if (super.timeIdle > 4500) {
			anInt1011 = 250;
			super.timeIdle -= 500;
			outgoing.writeOpcode(202);
		}
		anInt988++;
		if (anInt988 > 500) {
			anInt988 = 0;
			int l1 = (int) (Math.random() * 8D);
			if ((l1 & 1) == 1) {
				anInt1278 += anInt1279;
			}
			if ((l1 & 2) == 2) {
				anInt1131 += anInt1132;
			}
			if ((l1 & 4) == 4) {
				anInt896 += anInt897;
			}
		}
		if (anInt1278 < -50) {
			anInt1279 = 2;
		}
		if (anInt1278 > 50) {
			anInt1279 = -2;
		}
		if (anInt1131 < -55) {
			anInt1132 = 2;
		}
		if (anInt1131 > 55) {
			anInt1132 = -2;
		}
		if (anInt896 < -40) {
			anInt897 = 1;
		}
		if (anInt896 > 40) {
			anInt897 = -1;
		}
		anInt1254++;
		if (anInt1254 > 500) {
			anInt1254 = 0;
			int i2 = (int) (Math.random() * 8D);
			if ((i2 & 1) == 1) {
				anInt1209 += anInt1210;
			}
			if ((i2 & 2) == 2) {
				anInt1170 += anInt1171;
			}
		}
		if (anInt1209 < -60) {
			anInt1210 = 2;
		}
		if (anInt1209 > 60) {
			anInt1210 = -2;
		}
		if (anInt1170 < -20) {
			anInt1171 = 1;
		}
		if (anInt1170 > 10) {
			anInt1171 = -1;
		}
		anInt1010++;
		if (anInt1010 > 50) {
			outgoing.writeOpcode(0);
		}
		try {
			if (primary != null && outgoing.position > 0) {
				primary.write(outgoing.payload, outgoing.position, 0);
				outgoing.position = 0;
				anInt1010 = 0;
				return;
			}
		} catch (IOException _ex) {
			attemptReconnection();
		} catch (Exception exception) {
			reset();
		}
	}

	public final void pulseLoginScreen() {
		if (loginScreenStage == 0) {
			int loginButtonX = super.frameWidth / 2 - 80;
			int loginButtonY = super.frameHeight / 2 + 20;
			loginButtonY += 20;
			if (super.lastMetaModifier == 1 && super.lastClickX >= loginButtonX - 75 && super.lastClickX <= loginButtonX + 75
					&& super.lastClickY >= loginButtonY - 20 && super.lastClickY <= loginButtonY + 20) {
				loginScreenStage = 3;
				loginInputLine = 0;
			}
			loginButtonX = super.frameWidth / 2 + 80;
			if (super.lastMetaModifier == 1 && super.lastClickX >= loginButtonX - 75 && super.lastClickX <= loginButtonX + 75
					&& super.lastClickY >= loginButtonY - 20 && super.lastClickY <= loginButtonY + 20) {
				firstLoginMessage = "";
				secondLoginMessage = "Enter your username & password.";
				loginScreenStage = 2;
				loginInputLine = 0;
				return;
			}
		} else {
			if (loginScreenStage == 2) {
				int j = super.frameHeight / 2 - 40;
				j += 30;
				j += 25;
				if (super.lastMetaModifier == 1 && super.lastClickY >= j - 15 && super.lastClickY < j) {
					loginInputLine = 0;
				}
				j += 15;
				if (super.lastMetaModifier == 1 && super.lastClickY >= j - 15 && super.lastClickY < j) {
					loginInputLine = 1;
				}
				j += 15;
				int i1 = super.frameWidth / 2 - 80;
				int k1 = super.frameHeight / 2 + 50;
				k1 += 20;
				if (super.lastMetaModifier == 1 && super.lastClickX >= i1 - 75 && super.lastClickX <= i1 + 75
						&& super.lastClickY >= k1 - 20 && super.lastClickY <= k1 + 20) {
					loginFailures = 0;
					login(username, password, false);
					if (loggedIn) {
						return;
					}
				}
				i1 = super.frameWidth / 2 + 80;
				if (super.lastMetaModifier == 1 && super.lastClickX >= i1 - 75 && super.lastClickX <= i1 + 75
						&& super.lastClickY >= k1 - 20 && super.lastClickY <= k1 + 20) {
					loginScreenStage = 0;
				}
				do {
					int key = nextPressedKey();
					if (key == -1) {
						break;
					}
					boolean validCharacter = false;
					for (int i2 = 0; i2 < VALID_INPUT_CHARACTERS.length(); i2++) {
						if (key != VALID_INPUT_CHARACTERS.charAt(i2)) {
							continue;
						}
						validCharacter = true;
						break;
					}

					if (loginInputLine == 0) {
						if (key == 8 && username.length() > 0) {
							username = username.substring(0, username.length() - 1);
						}
						if (key == 9 || key == 10 || key == 13) {
							loginInputLine = 1;
						}
						if (validCharacter) {
							username += (char) key;
						}
						if (username.length() > 12) {
							username = username.substring(0, 12);
						}
					} else if (loginInputLine == 1) {
						if (key == 8 && password.length() > 0) {
							password = password.substring(0, password.length() - 1);
						}
						if (key == 9 || key == 10 || key == 13) {
							loginInputLine = 0;
						}
						if (validCharacter) {
							password += (char) key;
						}
						if (password.length() > 20) {
							password = password.substring(0, 20);
						}
					}
				} while (true);
				return;
			}
			if (loginScreenStage == 3) {
				int cancelX = super.frameWidth / 2;
				int cancelY = super.frameHeight / 2 + 50;
				cancelY += 20;
				if (super.lastMetaModifier == 1 && super.lastClickX >= cancelX - 75 && super.lastClickX <= cancelX + 75
						&& super.lastClickY >= cancelY - 20 && super.lastClickY <= cancelY + 20) {
					loginScreenStage = 0;
				}
			}
		}
	}

	public final void pulseMobChatText() {
		for (int i = -1; i < playerCount; i++) {
			int index;
			if (i == -1) {
				index = internalLocalPlayerIndex;
			} else {
				index = playerList[i];
			}
			Player player = players[index];
			if (player != null && player.textCycle > 0) {
				player.textCycle--;
				if (player.textCycle == 0) {
					player.spokenText = null;
				}
			}
		}

		for (int k = 0; k < npcCount; k++) {
			int index = npcList[k];
			Npc npc = npcs[index];
			if (npc != null && npc.textCycle > 0) {
				npc.textCycle--;
				if (npc.textCycle == 0) {
					npc.spokenText = null;
				}
			}
		}
	}

	public final void removeFriend(long l) {
		if (l == 0L) {
			return;
		}
		for (int i = 0; i < friendCount; i++) {
			if (friends[i] != l) {
				continue;
			}
			friendCount--;
			redrawTabArea = true;
			for (int j = i; j < friendCount; j++) {
				friendUsernames[j] = friendUsernames[j + 1];
				friendWorlds[j] = friendWorlds[j + 1];
				friends[j] = friends[j + 1];
			}

			outgoing.writeOpcode(215);
			outgoing.writeLong(l);
			break;
		}
	}

	public final DataInputStream requestCacheIndex(String request) throws IOException {
		if (jaggrabSocket != null) {
			try {
				jaggrabSocket.close();
			} catch (Exception _ex) {
			}
			jaggrabSocket = null;
		}

		jaggrabSocket = openSocket(43595);
		jaggrabSocket.setSoTimeout(10000);
		java.io.InputStream inputstream = jaggrabSocket.getInputStream();
		OutputStream outputstream = jaggrabSocket.getOutputStream();
		outputstream.write(("JAGGRAB /" + request + "\n\n").getBytes());
		return new DataInputStream(inputstream);
	}

	public final void requestCrcs() {
		int j = 5;
		archiveCRCs[8] = 0;
		int k = 0;
		while (archiveCRCs[8] == 0) {
			String s = "Unknown problem";
			drawLoadingText(20, "Connecting to web server");
			try {
				DataInputStream datainputstream = requestCacheIndex("crc" + (int) (Math.random() * 99999999D) + "-" + 317);
				Buffer buffer = new Buffer(new byte[40]);
				datainputstream.readFully(buffer.payload, 0, 40);
				datainputstream.close();
				for (int i1 = 0; i1 < 9; i1++) {
					archiveCRCs[i1] = buffer.readInt();
				}

				int j1 = buffer.readInt();
				int k1 = 1234;
				for (int l1 = 0; l1 < 9; l1++) {
					k1 = (k1 << 1) + archiveCRCs[l1];
				}

				if (j1 != k1) {
					s = "checksum problem";
					archiveCRCs[8] = 0;
				}
			} catch (EOFException _ex) {
				s = "EOF problem";
				archiveCRCs[8] = 0;
			} catch (IOException _ex) {
                _ex.printStackTrace();
				s = "connection problem";
				archiveCRCs[8] = 0;
			} catch (Exception _ex) {
				s = "logic problem";
				archiveCRCs[8] = 0;
				if (!SignLink.reportError) {
					return;
				}
			}
			if (archiveCRCs[8] == 0) {
				k++;
				for (int l = j; l > 0; l--) {
					if (k >= 10) {
						drawLoadingText(10, "Game updated - please reload page");
						l = 10;
					} else {
						drawLoadingText(10, s + " - Will retry in " + l + " secs.");
					}
					try {
						Thread.sleep(1000L);
					} catch (Exception _ex) {
					}
				}

				j *= 2;
				if (j > 60) {
					j = 60;
				}
				aBoolean872 = !aBoolean872;
			}
		}
	}

	public final void reset() {
		try {
			if (primary != null) {
				primary.stop();
			}
		} catch (Exception _ex) {
		}

		primary = null;
		loggedIn = false;
		windowResized = true;
		frame.setSize(DEFAULT_WIDTH + 8, DEFAULT_HEIGHT + 28);
		frame.setResizable(false);
		loginScreenStage = 0;
		unlinkCaches();
		scene.reset();

		for (int i = 0; i < 4; i++) {
			collisionMaps[i].init();
		}

		System.gc();
		stopMidi();
		nextMusicId = -1;
		musicId = -1;
		songDelay = 0;
	}

	public final void resetAnimation(int id) {
		Widget widget = Widget.widgets[id];
		for (int childId : widget.children) {
			if (childId == -1) {
				break;
			}
			Widget child = Widget.widgets[childId];
			if (child.type == 1) {
				resetAnimation(child.id);
			}

			child.displayedFrameCount = 0;
			child.lastFrameTime = 0;
		}
	}

	@Override
	public final void run() {
		if (aBoolean880) {
			method136();
		} else {
			super.run();
		}
	}

	public final void setWaveVolume(int volume) {
		SignLink.waveVolume = volume;
	}

	@Override
	public final void shutdown() {
		SignLink.reportError = false;
		try {
			if (primary != null) {
				primary.stop();
			}
		} catch (Exception _ex) {
		}
		primary = null;
		stopMidi();
		if (mouseCapturer != null) {
			mouseCapturer.running = false;
		}
		mouseCapturer = null;
		provider.stop();
		provider = null;
		chatBuffer = null;
		outgoing = null;
		loginBuffer = null;
		incomingBuffer = null;
		localRegionIds = null;
		localRegionMapData = null;
		localRegionLandscapeData = null;
		localRegionMapIds = null;
		localRegionLandscapeIds = null;
		anIntArrayArrayArray1214 = null;
		aByteArrayArrayArray1258 = null;
		scene = null;
		collisionMaps = null;
		anIntArrayArray901 = null;
		anIntArrayArray825 = null;
		waypointX = null;
		waypointY = null;
		aByteArray912 = null;
		aClass15_1163 = null;
		aClass15_1164 = null;
		aClass15_1165 = null;
		aClass15_1166 = null;
		aClass15_1123 = null;
		aClass15_1124 = null;
		aClass15_1125 = null;
		backLeft1Buffer = null;
		backLeft2Buffer = null;
		backRight1Buffer = null;
		backRight2Buffer = null;
		backTopBuffer = null;
		aClass15_908 = null;
		aClass15_909 = null;
		aClass15_910 = null;
		aClass15_911 = null;
		inventoryBackground = null;
		mapBackground = null;
		chatBackground = null;
		backBase1 = null;
		backBase2 = null;
		backHmid1 = null;
		sideIcons = null;
		aClass30_Sub2_Sub1_Sub2_1143 = null;
		aClass30_Sub2_Sub1_Sub2_1144 = null;
		aClass30_Sub2_Sub1_Sub2_1145 = null;
		aClass30_Sub2_Sub1_Sub2_1146 = null;
		aClass30_Sub2_Sub1_Sub2_1147 = null;
		aClass30_Sub2_Sub1_Sub2_865 = null;
		aClass30_Sub2_Sub1_Sub2_866 = null;
		aClass30_Sub2_Sub1_Sub2_867 = null;
		aClass30_Sub2_Sub1_Sub2_868 = null;
		aClass30_Sub2_Sub1_Sub2_869 = null;
		compass = null;
		hitMarks = null;
		headIcons = null;
		crosses = null;
		itemMapdot = null;
		npcMapdot = null;
		playerMapdot = null;
		friendMapdot = null;
		teamMapdot = null;
		mapScenes = null;
		mapFunctions = null;
		anIntArrayArray929 = null;
		players = null;
		playerList = null;
		mobsAwaitingUpdate = null;
		playerSynchronizationBuffers = null;
		anIntArray840 = null;
		npcs = null;
		npcList = null;
		groundItems = null;
		spawns = null;
		projectiles = null;
		incompleteAnimables = null;
		anIntArray1091 = null;
		anIntArray1092 = null;
		menuActionIds = null;
		anIntArray1094 = null;
		menuActionTexts = null;
		settings = null;
		anIntArray1072 = null;
		anIntArray1073 = null;
		aClass30_Sub2_Sub1_Sub1Array1140 = null;
		aClass30_Sub2_Sub1_Sub1_1263 = null;
		friendUsernames = null;
		friends = null;
		friendWorlds = null;
		aClass15_1110 = null;
		aClass15_1111 = null;
		aClass15_1107 = null;
		aClass15_1108 = null;
		aClass15_1109 = null;
		aClass15_1112 = null;
		aClass15_1113 = null;
		aClass15_1114 = null;
		aClass15_1115 = null;
		method118();
		ObjectDefinition.dispose();
		NpcDefinition.reset();
		ItemDefinition.dispose();
		Floor.floors = null;
		IdentityKit.kits = null;
		Widget.widgets = null;
		Animation.animations = null;
		Graphic.graphics = null;
		Graphic.models = null;
		VariableParameter.parameters = null;
		super.frameGraphicsBuffer = null;
		Player.models = null;
		Rasterizer.dispose();
		Scene.dispose();
		Model.dispose();
		Frame.clearFrames();
		System.gc();
	}

	@Override
	public final void startRunnable(Runnable runnable, int priority) {
		if (priority > 10) {
			priority = 10;
		}
		if (SignLink.applet != null) {
			SignLink.startThread(runnable, priority);
			return;
		}
		super.startRunnable(runnable, priority);
	}

	public final void stopMidi() {
		SignLink.midiFade = 0;
		SignLink.midi = "stop";
	}

	public final void removeIgnore(long encodedName) {
		try {
			if (encodedName == 0L) {
				return;
			}
			for (int j = 0; j < ignoredCount; j++) {
				if (ignoredPlayers[j] == encodedName) {
					ignoredCount--;
					redrawTabArea = true;
					for (int k = j; k < ignoredCount; k++) {
						ignoredPlayers[k] = ignoredPlayers[k + 1];
					}

					outgoing.writeOpcode(74);
					outgoing.writeLong(encodedName);
					return;
				}
			}

			return;
		} catch (RuntimeException runtimeexception) {
			SignLink.reportError("47229, " + encodedName + ", " + runtimeexception.toString());
		}
		throw new RuntimeException();
	}

	public final void unlinkCaches() {
		ObjectDefinition.baseModels.unlink();
		ObjectDefinition.models.unlink();
		NpcDefinition.modelCache.unlink();
		ItemDefinition.models.unlink();
		ItemDefinition.sprites.unlink();
		Player.models.unlink();
		Graphic.models.unlink();
	}

	public final void updateCharacters() {
		textCount = 0;
		for (int j = -1; j < playerCount + npcCount; j++) {
			Mob mob;
			if (j == -1) {
				mob = localPlayer;
			} else if (j < playerCount) {
				mob = players[playerList[j]];
			} else {
				mob = npcs[npcList[j - playerCount]];
			}
			if (mob == null || !mob.isVisible()) {
				continue;
			}
			if (mob instanceof Npc) {
				NpcDefinition definition = ((Npc) mob).definition;
				if (definition.morphisms != null) {
					definition = definition.morph();
				}
				if (definition == null) {
					continue;
				}
			}
			if (j < playerCount) {
				int l = 30;
				Player player = (Player) mob;
				if (player.headIcon != 0) {
					method127(mob, mob.height + 15);
					if (spriteDrawX > -1) {
						for (int i2 = 0; i2 < 8; i2++) {
							if ((player.headIcon & 1 << i2) != 0) {
								headIcons[i2].drawSprite(spriteDrawX - 12, spriteDrawY - l);
								l -= 25;
							}
						}

					}
				}
				if (j >= 0 && headIconDrawType == 10 && lastInteractedWithPlayer == playerList[j]) {
					method127(mob, mob.height + 15);
					if (spriteDrawX > -1) {
						headIcons[7].drawSprite(spriteDrawX - 12, spriteDrawY - l);
					}
				}
			} else {
				NpcDefinition definition = ((Npc) mob).definition;
				if (definition.headIcon >= 0 && definition.headIcon < headIcons.length) {
					method127(mob, mob.height + 15);
					if (spriteDrawX > -1) {
						headIcons[definition.headIcon].drawSprite(spriteDrawX - 12, spriteDrawY - 30);
					}
				}
				if (headIconDrawType == 1 && anInt1222 == npcList[j - playerCount] && tick % 20 < 10) {
					method127(mob, mob.height + 15);
					if (spriteDrawX > -1) {
						headIcons[2].drawSprite(spriteDrawX - 12, spriteDrawY - 28);
					}
				}
			}
			if (mob.spokenText != null
					&& (j >= playerCount || publicChatMode == 0 || publicChatMode == 3 || publicChatMode == 1
							&& displayMessageFrom(((Player) mob).name))) {
				method127(mob, mob.height);
				if (spriteDrawX > -1 && textCount < maxTextCount) {
					spokenTextWidth[textCount] = boldFont.getExactTextWidth(mob.spokenText) / 2;
					spokenTextHeight[textCount] = boldFont.verticalSpace;
					spokenTextX[textCount] = spriteDrawX;
					spokenTextY[textCount] = spriteDrawY;
					spokenTextColour[textCount] = mob.textColour;
					spokenTextEffect[textCount] = mob.textEffect;
					spokenTextCycle[textCount] = mob.textCycle;
					spokenText[textCount++] = mob.spokenText;
					if (chatEffects == 0 && mob.textEffect >= 1 && mob.textEffect <= 3) {
						spokenTextHeight[textCount] += 10;
						spokenTextY[textCount] += 5;
					}
					if (chatEffects == 0 && mob.textEffect == 4) {
						spokenTextWidth[textCount] = 60;
					}
					if (chatEffects == 0 && mob.textEffect == 5) {
						spokenTextHeight[textCount] += 5;
					}
				}
			}
			if (mob.cycleStatus > tick) {
				method127(mob, mob.height + 15);
				if (spriteDrawX > -1) {
					int i1 = mob.currentHealth * 30 / mob.maximumHealth;
					if (i1 > 30) {
						i1 = 30;
					}
					Raster.fillRectangle(spriteDrawX - 15, spriteDrawY - 3, 5, i1, 65280);
					Raster.fillRectangle(spriteDrawX - 15 + i1, spriteDrawY - 3, 5, 30 - i1, 0xff0000);
				}
			}
			for (int j1 = 0; j1 < 4; j1++) {
				if (mob.hitCycles[j1] > tick) {
					method127(mob, mob.height / 2);
					if (spriteDrawX > -1) {
						if (j1 == 1) {
							spriteDrawY -= 20;
						}
						if (j1 == 2) {
							spriteDrawX -= 15;
							spriteDrawY -= 10;
						}
						if (j1 == 3) {
							spriteDrawX += 15;
							spriteDrawY -= 10;
						}
						hitMarks[mob.hitTypes[j1]].drawSprite(spriteDrawX - 12, spriteDrawY - 12);
						smallFont.renderCentre(spriteDrawX, spriteDrawY + 4, String.valueOf(mob.hitDamages[j1]), 0);
						smallFont.renderCentre(spriteDrawX - 1, spriteDrawY + 3, String.valueOf(mob.hitDamages[j1]), 0xffffff);
					}
				}
			}
		}

		for (int message = 0; message < textCount; message++) {
			int k1 = spokenTextX[message];
			int l1 = spokenTextY[message];
			int j2 = spokenTextWidth[message];
			int k2 = spokenTextHeight[message];
			boolean flag = true;
			while (flag) {
				flag = false;
				for (int l2 = 0; l2 < message; l2++) {
					if (l1 + 2 > spokenTextY[l2] - spokenTextHeight[l2] && l1 - k2 < spokenTextY[l2] + 2
							&& k1 - j2 < spokenTextX[l2] + spokenTextWidth[l2] && k1 + j2 > spokenTextX[l2] - spokenTextWidth[l2]
							&& spokenTextY[l2] - spokenTextHeight[l2] < l1) {
						l1 = spokenTextY[l2] - spokenTextHeight[l2];
						flag = true;
					}
				}

			}
			spriteDrawX = spokenTextX[message];
			spriteDrawY = spokenTextY[message] = l1;
			String chatMessage = spokenText[message];
			if (chatEffects == 0) {
				int textColour = 0xffff00;
				if (spokenTextColour[message] < 6) {
					textColour = textColours[spokenTextColour[message]];
				}
				if (spokenTextColour[message] == 6) {
					textColour = anInt1265 % 20 >= 10 ? 0xffff00 : 0xff0000;
				}
				if (spokenTextColour[message] == 7) {
					textColour = anInt1265 % 20 >= 10 ? 65535 : 255;
				}
				if (spokenTextColour[message] == 8) {
					textColour = anInt1265 % 20 >= 10 ? 0x80ff80 : 45056;
				}
				if (spokenTextColour[message] == 9) {
					int j3 = 150 - spokenTextCycle[message];
					if (j3 < 50) {
						textColour = 0xff0000 + 1280 * j3;
					} else if (j3 < 100) {
						textColour = 0xffff00 - 0x50000 * (j3 - 50);
					} else if (j3 < 150) {
						textColour = 65280 + 5 * (j3 - 100);
					}
				}
				if (spokenTextColour[message] == 10) {
					int k3 = 150 - spokenTextCycle[message];
					if (k3 < 50) {
						textColour = 0xff0000 + 5 * k3;
					} else if (k3 < 100) {
						textColour = 0xff00ff - 0x50000 * (k3 - 50);
					} else if (k3 < 150) {
						textColour = 255 + 0x50000 * (k3 - 100) - 5 * (k3 - 100);
					}
				}
				if (spokenTextColour[message] == 11) {
					int l3 = 150 - spokenTextCycle[message];
					if (l3 < 50) {
						textColour = 0xffffff - 0x50005 * l3;
					} else if (l3 < 100) {
						textColour = 65280 + 0x50005 * (l3 - 50);
					} else if (l3 < 150) {
						textColour = 0xffffff - 0x50000 * (l3 - 100);
					}
				}
				if (spokenTextEffect[message] == 0) {
					boldFont.renderCentre(spriteDrawX, spriteDrawY + 1, chatMessage, 0);
					boldFont.renderCentre(spriteDrawX, spriteDrawY, chatMessage, textColour);
				}
				if (spokenTextEffect[message] == 1) {
					boldFont.wave(chatMessage, spriteDrawX, spriteDrawY + 1, 0, anInt1265);
					boldFont.wave(chatMessage, spriteDrawX, spriteDrawY, textColour, anInt1265);
				}
				if (spokenTextEffect[message] == 2) {
					boldFont.wave2(chatMessage, spriteDrawX, spriteDrawY + 1, 0, anInt1265);
					boldFont.wave2(chatMessage, spriteDrawX, spriteDrawY, textColour, anInt1265);
				}
				if (spokenTextEffect[message] == 3) {
					boldFont.shake(chatMessage, spriteDrawX, spriteDrawY + 1, 0, 150 - spokenTextCycle[message], anInt1265);
					boldFont.shake(chatMessage, spriteDrawX, spriteDrawY, textColour, 150 - spokenTextCycle[message], anInt1265);
				}
				if (spokenTextEffect[message] == 4) {
					int i4 = boldFont.getExactTextWidth(chatMessage);
					int k4 = (150 - spokenTextCycle[message]) * (i4 + 100) / 150;
					Raster.setBounds(334, spriteDrawX - 50, spriteDrawX + 50, 0);
					boldFont.render(spriteDrawX + 50 - k4, spriteDrawY + 1, chatMessage, 0);
					boldFont.render(spriteDrawX + 50 - k4, spriteDrawY, chatMessage, textColour);
					Raster.setDefaultBounds();
				}
				if (spokenTextEffect[message] == 5) {
					int j4 = 150 - spokenTextCycle[message];
					int l4 = 0;
					if (j4 < 25) {
						l4 = j4 - 25;
					} else if (j4 > 125) {
						l4 = j4 - 125;
					}
					Raster.setBounds(spriteDrawY + 5, 0, 512, spriteDrawY - boldFont.verticalSpace - 1);
					boldFont.renderCentre(spriteDrawX, spriteDrawY + 1 + l4, chatMessage, 0);
					boldFont.renderCentre(spriteDrawX, spriteDrawY + l4, chatMessage, textColour);
					Raster.setDefaultBounds();
				}
			} else {
				boldFont.renderCentre(spriteDrawX, spriteDrawY + 1, chatMessage, 0);
				boldFont.renderCentre(spriteDrawX, spriteDrawY, chatMessage, 0xffff00);
			}
		}

	}

	public final boolean waveReplay() {
		return SignLink.waveReplay();
	}

	public final boolean waveSave(byte buffer[], int length) {
		if (buffer == null) {
			return true;
		}

		return SignLink.waveSave(buffer, length);
	}

	private final void error(String s) {
		System.err.println("ERROR: " + s);
		try {
			getAppletContext().showDocument(new URL(getCodeBase(), "loaderror_" + s + ".html"));
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		do {
			try {
				Thread.sleep(1000L);
			} catch (Exception _ex) {
			}
		} while (true);
	}

	private final void method115() {
		if (loadingStage == 2) {
			for (ObjectSpawn spawn = (ObjectSpawn) spawns.getFront(); spawn != null; spawn = (ObjectSpawn) spawns.getNext()) {
				if (spawn.longetivity > 0) {
					spawn.longetivity--;
				}

				if (spawn.longetivity == 0) {
					if (spawn.anInt1303 < 0 || Region.modelReady(spawn.anInt1303, spawn.anInt1304)) {
						method142(spawn.x, spawn.y, spawn.plane, spawn.type, spawn.anInt1300, spawn.anInt1304, spawn.anInt1303);
						spawn.unlink();
					}
				} else {
					if (spawn.anInt1302 > 0) {
						spawn.anInt1302--;
					}
					if (spawn.anInt1302 == 0 && spawn.x >= 1 && spawn.y >= 1 && spawn.x <= 102 && spawn.y <= 102
							&& (spawn.id < 0 || Region.modelReady(spawn.id, spawn.anInt1293))) {
						method142(spawn.x, spawn.y, spawn.plane, spawn.type, spawn.orientation, spawn.anInt1293, spawn.id);
						spawn.anInt1302 = -1;
						if (spawn.id == spawn.anInt1303 && spawn.anInt1303 == -1) {
							spawn.unlink();
						} else if (spawn.id == spawn.anInt1303 && spawn.orientation == spawn.anInt1300
								&& spawn.anInt1293 == spawn.anInt1304) {
							spawn.unlink();
						}
					}
				}
			}
		}
	}

	private final void method130(int id, int x, int y, int plane, int type, int orientation, int longetivity, int k1, int j2) {
		ObjectSpawn object = null;
		for (ObjectSpawn node = (ObjectSpawn) spawns.getFront(); node != null; node = (ObjectSpawn) spawns.getNext()) {
			if (node.plane != plane || node.x != x || node.y != y || node.type != type) {
				continue;
			}
			object = node;
			break;
		}

		if (object == null) {
			object = new ObjectSpawn();
			object.plane = plane;
			object.type = type;
			object.x = x;
			object.y = y;
			method89(object);
			spawns.pushBack(object);
		}
		object.id = id;
		object.anInt1293 = k1;
		object.orientation = orientation;
		object.anInt1302 = j2;
		object.longetivity = longetivity;
	}

	private final void method142(int x, int y, int z, int type, int k, int l, int k1) {
		if (x >= 1 && y >= 1 && x <= 102 && y <= 102) {
			if (lowMemory && z != plane) {
				return;
			}
			int key = 0;
			if (type == 0) {
				key = scene.getWallKey(x, y, z);
			}
			if (type == 1) {
				key = scene.getWallDecorationKey(x, y, z);
			}
			if (type == 2) {
				key = scene.getInteractableObjectKey(x, y, z);
			}
			if (type == 3) {
				key = scene.getFloorDecorationKey(x, y, z);
			}
			if (key != 0) {
				int config = scene.getConfig(x, y, z, key);
				int id = key >> 14 & 0x7fff;
				int objectType = config & 0x1f;
				int orientation = config >> 6;

				if (type == 0) {
					scene.clearWall(x, y, z);
					ObjectDefinition definition = ObjectDefinition.lookup(id);
					if (definition.solid) {
						collisionMaps[z].removeObject(x, y, orientation, objectType, definition.impenetrable);
					}
				} else if (type == 1) {
					scene.clearWallDecoration(x, y, z);
				} else if (type == 2) {
					scene.method293(x, y, z);
					ObjectDefinition definition = ObjectDefinition.lookup(id);
					if (x + definition.width > 103 || y + definition.width > 103 || x + definition.length > 103
							|| y + definition.length > 103) {
						return;
					}
					if (definition.solid) {
						collisionMaps[z].removeObject(orientation, definition.width, x, y, definition.length,
								definition.impenetrable);
					}
				} else if (type == 3) {
					scene.clearFloorDecoration(x, y, z);
					ObjectDefinition definition = ObjectDefinition.lookup(id);
					if (definition.solid && definition.interactive) {
						collisionMaps[z].removeFloorDecoration(x, y);
					}
				}
			}

			if (k1 >= 0) {
				int plane = z;
				if (plane < 3 && (aByteArrayArrayArray1258[1][x][y] & 2) == 2) {
					plane++;
				}
				Region.method188(scene, k, y, l, plane, collisionMaps[z], anIntArrayArrayArray1214, x, k1, z);
			}
		}
	}

	private final void method63() {
		ObjectSpawn spawn = (ObjectSpawn) spawns.getFront();
		for (; spawn != null; spawn = (ObjectSpawn) spawns.getNext()) {
			if (spawn.longetivity == -1) {
				spawn.anInt1302 = 0;
				method89(spawn);
			} else {
				spawn.unlink();
			}
		}

	}

	private final void method89(ObjectSpawn spawn) {
		int key = 0;
		int id = -1;
		int type = 0;
		int orientation = 0;
		if (spawn.type == 0) {
			key = scene.getWallKey(spawn.x, spawn.y, spawn.plane);
		}
		if (spawn.type == 1) {
			key = scene.getWallDecorationKey(spawn.x, spawn.y, spawn.plane);
		}
		if (spawn.type == 2) {
			key = scene.getInteractableObjectKey(spawn.x, spawn.y, spawn.plane);
		}
		if (spawn.type == 3) {
			key = scene.getFloorDecorationKey(spawn.x, spawn.y, spawn.plane);
		}
		if (key != 0) {
			int config = scene.getConfig(spawn.x, spawn.y, spawn.plane, key);
			id = key >> 14 & 0x7fff;
			type = config & 0x1f;
			orientation = config >> 6;
		}
		spawn.anInt1303 = id;
		spawn.anInt1304 = type;
		spawn.anInt1300 = orientation;
	}

	private final void parsePlayerSynchronizationMask(Buffer buffer) {
		for (int i = 0; i < mobsAwaitingUpdateCount; i++) {
			int index = mobsAwaitingUpdate[i];
			Player player = players[index];
			int mask = buffer.readUByte();
			if ((mask & 0x40) != 0) {
				mask += buffer.readUByte() << 8;
			}

			processPlayerSynchronizationMask(buffer, player, index, mask);
		}
	}

	private final void processNpcSynchronizationMask(Buffer buffer) {
		for (int i = 0; i < mobsAwaitingUpdateCount; i++) {
			int index = mobsAwaitingUpdate[i];
			Npc npc = npcs[index];
			int mask = buffer.readUByte();

			if ((mask & 0x10) != 0) {
				int animation = buffer.readLEUShort();
				if (animation == 65535) {
					animation = -1;
				}

				int delay = buffer.readUByte();

				if (animation == npc.emoteAnimation && animation != -1) {
					int replayMode = Animation.animations[animation].replayMode;

					if (replayMode == 1) {
						npc.displayedEmoteFrames = 0;
						npc.anInt1528 = 0;
						npc.animationDelay = delay;
						npc.currentAnimationLoops = 0;
					} else if (replayMode == 2) {
						npc.currentAnimationLoops = 0;
					}
				} else if (animation == -1 || npc.emoteAnimation == -1
						|| Animation.animations[animation].priority >= Animation.animations[npc.emoteAnimation].priority) {
					npc.emoteAnimation = animation;
					npc.displayedEmoteFrames = 0;
					npc.anInt1528 = 0;
					npc.animationDelay = delay;
					npc.currentAnimationLoops = 0;
					npc.anInt1542 = npc.remainingPath;
				}
			}

			if ((mask & 8) != 0) {
				int damage = buffer.readUByteA();
				int type = buffer.readNegUByte();
				npc.damage(damage, type, tick);
				npc.cycleStatus = tick + 300;
				npc.currentHealth = buffer.readUByteA();
				npc.maximumHealth = buffer.readUByte();
			}

			if ((mask & 0x80) != 0) {
				npc.graphicId = buffer.readUShort();
				int info = buffer.readInt();
				npc.graphicHeight = info >> 16;
				npc.graphicDelay = tick + (info & 0xffff);
				npc.currentAnimation = 0;
				npc.anInt1522 = 0;

				if (npc.graphicDelay > tick) {
					npc.currentAnimation = -1;
				}

				if (npc.graphicId == 65535) {
					npc.graphicId = -1;
				}
			}

			if ((mask & 0x20) != 0) {
				npc.interactingMob = buffer.readUShort();
				if (npc.interactingMob == 65535) {
					npc.interactingMob = -1;
				}
			}

			if ((mask & 1) != 0) {
				npc.spokenText = buffer.readString();
				npc.textCycle = 100;
			}

			if ((mask & 0x40) != 0) {
				int damage = buffer.readNegUByte();
				int type = buffer.readUByteS();
				npc.damage(damage, type, tick);
				npc.cycleStatus = tick + 300;
				npc.currentHealth = buffer.readUByteS();
				npc.maximumHealth = buffer.readNegUByte();
			}

			if ((mask & 2) != 0) {
				npc.definition = NpcDefinition.lookup(buffer.readLEUShortA());
				npc.size = npc.definition.size;
				npc.rotation = npc.definition.rotation;
				npc.walkingAnimation = npc.definition.walkingAnimation;
				npc.halfTurnAnimation = npc.definition.halfTurnAnimation;
				npc.quarterClockwiseTurnAnimation = npc.definition.rotateClockwiseAnimation;
				npc.quarterAnticlockwiseTurnAnimation = npc.definition.rotateAntiClockwiseAnimation;
				npc.idleAnimation = npc.definition.idleAnimation;
			}

			if ((mask & 4) != 0) {
				npc.faceX = buffer.readLEUShort();
				npc.faceY = buffer.readLEUShort();
			}
		}
	}

	private final void processPlayerSynchronizationMask(Buffer buffer, Player player, int index, int mask) {
		if ((mask & 0x400) != 0) {
			player.initialX = buffer.readUByteS();
			player.initialY = buffer.readUByteS();
			player.destinationX = buffer.readUByteS();
			player.destinationY = buffer.readUByteS();
			player.startForceMovement = buffer.readLEUShortA() + tick;
			player.endForceMovement = buffer.readUShortA() + tick;
			player.direction = buffer.readUByteS();
			player.resetPath();
		}

		if ((mask & 0x100) != 0) {
			player.graphicId = buffer.readLEUShort();
			int info = buffer.readInt();
			player.graphicHeight = info >> 16;
			player.graphicDelay = tick + (info & 0xffff);
			player.currentAnimation = 0;
			player.anInt1522 = 0;

			if (player.graphicDelay > tick) {
				player.currentAnimation = -1;
			}
			if (player.graphicId == 65535) {
				player.graphicId = -1;
			}
		}

		if ((mask & 8) != 0) {
			int animation = buffer.readLEUShort();
			if (animation == 65535) {
				animation = -1;
			}

			int delay = buffer.readNegUByte();
			if (animation == player.emoteAnimation && animation != -1) {
				int replayMode = Animation.animations[animation].replayMode;
				if (replayMode == 1) {
					player.displayedEmoteFrames = 0;
					player.anInt1528 = 0;
					player.animationDelay = delay;
					player.currentAnimationLoops = 0;
				} else if (replayMode == 2) {
					player.currentAnimationLoops = 0;
				}
			} else if (animation == -1 || player.emoteAnimation == -1
					|| Animation.animations[animation].priority >= Animation.animations[player.emoteAnimation].priority) {
				player.emoteAnimation = animation;
				player.displayedEmoteFrames = 0;
				player.anInt1528 = 0;
				player.animationDelay = delay;
				player.currentAnimationLoops = 0;
				player.anInt1542 = player.remainingPath;
			}
		}

		if ((mask & 4) != 0) {
			player.spokenText = buffer.readString();

			if (player.spokenText.charAt(0) == '~') {
				player.spokenText = player.spokenText.substring(1);
				addChatMessage(2, player.spokenText, player.name);
			} else if (player == localPlayer) {
				addChatMessage(2, player.spokenText, player.name);
			}
			player.textColour = 0;
			player.textEffect = 0;
			player.textCycle = 150;
		}

		if ((mask & 0x80) != 0) {
			int textInfo = buffer.readLEUShort();
			int privilege = buffer.readUByte();
			int offset = buffer.readNegUByte();
			int off = buffer.position;

			if (player.name != null && player.visible) {
				long name = StringUtils.encodeBase37(player.name);
				boolean ignored = false;
				if (privilege <= 1) {
					for (int i4 = 0; i4 < ignoredCount; i4++) {
						if (ignoredPlayers[i4] != name) {
							continue;
						}
						ignored = true;
						break;
					}
				}

				if (!ignored && onTutorialIsland == 0) {
					try {
						chatBuffer.position = 0;
						buffer.readReverseData(chatBuffer.payload, offset, 0);
						chatBuffer.position = 0;
						String text = ChatMessageCodec.decode(chatBuffer, offset);
						text = MessageCensor.apply(text);
						player.spokenText = text;
						player.textColour = textInfo >> 8;
						player.textEffect = textInfo & 0xff;
						player.textCycle = 150;

						if (privilege == 2 || privilege == 3) {
							addChatMessage(1, text, "@cr2@" + player.name);
						} else if (privilege == 1) {
							addChatMessage(1, text, "@cr1@" + player.name);
						} else {
							addChatMessage(2, text, player.name);
						}
					} catch (Exception exception) {
						SignLink.reportError("cde2");
					}
				}
			}

			buffer.position = off + offset;
		}

		if ((mask & 1) != 0) {
			player.interactingMob = buffer.readLEUShort();
			if (player.interactingMob == 65535) {
				player.interactingMob = -1;
			}
		}

		if ((mask & 0x10) != 0) {
			int length = buffer.readNegUByte();
			byte[] buf = new byte[length];
			Buffer appearanceBuffer = new Buffer(buf);
			buffer.readData(buf, 0, length);
			playerSynchronizationBuffers[index] = appearanceBuffer;
			player.updateAppearance(appearanceBuffer);
		}

		if ((mask & 2) != 0) {
			player.faceX = buffer.readLEUShortA();
			player.faceY = buffer.readLEUShort();
		}

		if ((mask & 0x20) != 0) {
			int damage = buffer.readUByte();
			int type = buffer.readUByteA();
			player.damage(damage, type, tick);
			player.cycleStatus = tick + 300;
			player.currentHealth = buffer.readNegUByte();
			player.maximumHealth = buffer.readUByte();
		}

		if ((mask & 0x200) != 0) {
			int damage = buffer.readUByte();
			int type = buffer.readUByteS();
			player.damage(damage, type, tick);
			player.cycleStatus = tick + 300;
			player.currentHealth = buffer.readUByte();
			player.maximumHealth = buffer.readNegUByte();
		}
	}

	private final void synchronizeLocalPlayerMovement(Buffer buffer) {
		buffer.enableBitAccess();
		int update = buffer.readBits(1);
		if (update == 0) {
			return;
		}

		int movementType = buffer.readBits(2);
		if (movementType == 0) {
			mobsAwaitingUpdate[mobsAwaitingUpdateCount++] = internalLocalPlayerIndex;
		} else if (movementType == 1) {
			int direction = buffer.readBits(3);
			localPlayer.setPosition(direction, false);
			int updateRequired = buffer.readBits(1);

			if (updateRequired == 1) {
				mobsAwaitingUpdate[mobsAwaitingUpdateCount++] = internalLocalPlayerIndex;
			}
		} else if (movementType == 2) {
			int firstDirection = buffer.readBits(3);
			localPlayer.setPosition(firstDirection, true);
			int secondDirection = buffer.readBits(3);
			localPlayer.setPosition(secondDirection, true);
			int updateRequired = buffer.readBits(1);

			if (updateRequired == 1) {
				mobsAwaitingUpdate[mobsAwaitingUpdateCount++] = internalLocalPlayerIndex;
			}
		} else if (movementType == 3) {
			plane = buffer.readBits(2);
			int teleport = buffer.readBits(1);
			int updateRequired = buffer.readBits(1);

			if (updateRequired == 1) {
				mobsAwaitingUpdate[mobsAwaitingUpdateCount++] = internalLocalPlayerIndex;
			}

			int x = buffer.readBits(7);
			int y = buffer.readBits(7);
			localPlayer.move(y, x, teleport == 1);
		}
	}

	private final void synchronizeNpcs(Buffer buffer, int packetSize) {
		anInt839 = 0;
		mobsAwaitingUpdateCount = 0;
		updateNpcMovement(buffer);
		updateNpcList(buffer, packetSize);
		processNpcSynchronizationMask(buffer);
		for (int i = 0; i < anInt839; i++) {
			int index = anIntArray840[i];
			if (npcs[index].time != tick) {
				npcs[index].definition = null;
				npcs[index] = null;
			}
		}

		if (buffer.position != packetSize) {
			SignLink.reportError(username + " size mismatch in getnpcpos - pos:" + buffer.position + " psize:" + packetSize);
			throw new RuntimeException("eek");
		}
		for (int i1 = 0; i1 < npcCount; i1++) {
			if (npcs[npcList[i1]] == null) {
				SignLink.reportError(username + " null entry in npc list - pos:" + i1 + " size:" + npcCount);
				throw new RuntimeException("eek");
			}
		}

	}

	private final void synchronizeOtherPlayerMovement(Buffer buffer) {
		int count = buffer.readBits(8);
		if (count < playerCount) {
			for (int i = count; i < playerCount; i++) {
				anIntArray840[anInt839++] = playerList[i];
			}
		}

		if (count > playerCount) {
			SignLink.reportError(username + " Too many players");
			throw new RuntimeException("eek");
		}

		playerCount = 0;
		for (int i = 0; i < count; i++) {
			int index = playerList[i];
			Player player = players[index];

			int updateRequired = buffer.readBits(1);
			if (updateRequired == 0) {
				playerList[playerCount++] = index;
				player.time = tick;
			} else {
				int movementType = buffer.readBits(2);

				if (movementType == 0) {
					playerList[playerCount++] = index;
					player.time = tick;
					mobsAwaitingUpdate[mobsAwaitingUpdateCount++] = index;
				} else if (movementType == 1) {
					playerList[playerCount++] = index;
					player.time = tick;
					int direction = buffer.readBits(3);
					player.setPosition(direction, false);
					int update = buffer.readBits(1);

					if (update == 1) {
						mobsAwaitingUpdate[mobsAwaitingUpdateCount++] = index;
					}
				} else if (movementType == 2) {
					playerList[playerCount++] = index;
					player.time = tick;
					int firstDirection = buffer.readBits(3);
					player.setPosition(firstDirection, true);
					int secondDirection = buffer.readBits(3);
					player.setPosition(secondDirection, true);
					int update = buffer.readBits(1);

					if (update == 1) {
						mobsAwaitingUpdate[mobsAwaitingUpdateCount++] = index;
					}
				} else if (movementType == 3) {
					anIntArray840[anInt839++] = index;
				}
			}
		}
	}

	private final void synchronizePlayers(int frameSize, Buffer buffer) {
		anInt839 = 0;
		mobsAwaitingUpdateCount = 0;
		synchronizeLocalPlayerMovement(buffer);
		synchronizeOtherPlayerMovement(buffer);
		updatePlayerList(buffer, frameSize);
		parsePlayerSynchronizationMask(buffer);
		for (int i = 0; i < anInt839; i++) {
			int index = anIntArray840[i];
			if (players[index].time != tick) {
				players[index] = null;
			}
		}

		if (buffer.position != frameSize) {
			SignLink.reportError("Error packet size mismatch in getplayer pos:" + buffer.position + " psize:" + frameSize);
			throw new RuntimeException("eek");
		}

		for (int i = 0; i < playerCount; i++) {
			if (players[playerList[i]] == null) {
				SignLink.reportError(username + " null entry in pl list - pos:" + i + " size:" + playerCount);
				throw new RuntimeException("eek");
			}
		}
	}

	private final void updateNpcList(Buffer buffer, int packetSize) {
		while (buffer.bitPosition + 21 < packetSize * 8) {
			int index = buffer.readBits(14);
			if (index == 16383) {
				break;
			}

			if (npcs[index] == null) {
				npcs[index] = new Npc();
			}

			Npc npc = npcs[index];
			npcList[npcCount++] = index;
			npc.time = tick;

			int y = buffer.readBits(5);
			if (y > 15) {
				y -= 32;
			}

			int x = buffer.readBits(5);
			if (x > 15) {
				x -= 32;
			}

			int teleport = buffer.readBits(1);
			npc.definition = NpcDefinition.lookup(buffer.readBits(12));
			int updateRequired = buffer.readBits(1);
			if (updateRequired == 1) {
				mobsAwaitingUpdate[mobsAwaitingUpdateCount++] = index;
			}

			npc.size = npc.definition.size;
			npc.rotation = npc.definition.rotation;
			npc.walkingAnimation = npc.definition.walkingAnimation;
			npc.halfTurnAnimation = npc.definition.halfTurnAnimation;
			npc.quarterClockwiseTurnAnimation = npc.definition.rotateClockwiseAnimation;
			npc.quarterAnticlockwiseTurnAnimation = npc.definition.rotateAntiClockwiseAnimation;
			npc.idleAnimation = npc.definition.idleAnimation;
			npc.move(localPlayer.pathX[0] + x, localPlayer.pathY[0] + y, teleport == 1);
		}
		buffer.disableBitAccess();
	}

	private final void updateNpcMovement(Buffer buffer) {
		buffer.enableBitAccess();
		int count = buffer.readBits(8);
		if (count < npcCount) {
			for (int i = count; i < npcCount; i++) {
				anIntArray840[anInt839++] = npcList[i];
			}

		}
		if (count > npcCount) {
			SignLink.reportError(username + " Too many npcs");
			throw new RuntimeException("eek");
		}

		npcCount = 0;
		for (int i = 0; i < count; i++) {
			int index = npcList[i];
			Npc npc = npcs[index];
			int update = buffer.readBits(1);

			if (update == 0) {
				npcList[npcCount++] = index;
				npc.time = tick;
			} else {
				int l1 = buffer.readBits(2);
				if (l1 == 0) {
					npcList[npcCount++] = index;
					npc.time = tick;
					mobsAwaitingUpdate[mobsAwaitingUpdateCount++] = index;
				} else if (l1 == 1) {
					npcList[npcCount++] = index;
					npc.time = tick;
					int direction = buffer.readBits(3);
					npc.setPosition(direction, false);
					int updateRequired = buffer.readBits(1);
					if (updateRequired == 1) {
						mobsAwaitingUpdate[mobsAwaitingUpdateCount++] = index;
					}
				} else if (l1 == 2) {
					npcList[npcCount++] = index;
					npc.time = tick;
					int dir1 = buffer.readBits(3);
					npc.setPosition(dir1, true);
					int dir2 = buffer.readBits(3);
					npc.setPosition(dir2, true);
					int updateRequired = buffer.readBits(1);
					if (updateRequired == 1) {
						mobsAwaitingUpdate[mobsAwaitingUpdateCount++] = index;
					}
				} else if (l1 == 3) {
					anIntArray840[anInt839++] = index;
				}
			}
		}

	}

	private final void updatePlayerList(Buffer buffer, int packetSize) {
		while (buffer.bitPosition + 10 < packetSize * 8) {
			int index = buffer.readBits(11);
			if (index == 2047) {
				break;
			}
			if (players[index] == null) {
				players[index] = new Player();
				if (playerSynchronizationBuffers[index] != null) {
					players[index].updateAppearance(playerSynchronizationBuffers[index]);
				}
			}
			playerList[playerCount++] = index;
			Player player = players[index];
			player.time = tick;
			int k = buffer.readBits(1);
			if (k == 1) {
				mobsAwaitingUpdate[mobsAwaitingUpdateCount++] = index;
			}
			int discardWalkingQueue = buffer.readBits(1);
			int y = buffer.readBits(5);
			if (y > 15) {
				y -= 32;
			}
			int x = buffer.readBits(5);
			if (x > 15) {
				x -= 32;
			}
			player.move(localPlayer.pathX[0] + x, localPlayer.pathY[0] + y, discardWalkingQueue == 1);
		}

		buffer.disableBitAccess();
	}

}
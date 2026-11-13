const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";

const [emulatorHost, emulatorPortStr] = (
  process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085"
).split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

const pastTimestamp = () => new Date(Date.now() - 2000);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated user cannot read or write profiles, posts, or check-ins", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).get());
  await assertFails(
    unauthDb.collection("posts").where("visibility", "==", "PUBLIC").get()
  );
  await assertFails(
    unauthDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("check_ins")
      .doc("2026-11-12")
      .get()
  );
});

test("User profile enforces strict owner isolation and rejects ghost fields", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      displayName: "Alice Mindful",
      handle: "alicemindful",
      bio: "Exploring positive psychology",
      currentStreak: 1,
      longestStreak: 1,
      totalCheckIns: 1,
      createdAt: pastTimestamp(),
      updatedAt: pastTimestamp(),
    })
  );

  // Cross-user read & write rejected
  await assertFails(bobDb.collection("users").doc(ALICE_UID).get());
  await assertFails(
    bobDb.collection("users").doc(ALICE_UID).update({ displayName: "Hacked" })
  );

  // Shadow/ghost field rejected
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).update({ isAdmin: true })
  );
});

test("Private mood check-ins are strictly isolated to owner", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();

  await assertSucceeds(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("check_ins")
      .doc("2026-11-12")
      .set({
        userId: ALICE_UID,
        dateKey: "2026-11-12",
        moodKey: "CALM",
        moodEmoji: "😌",
        reflection: "Took a quiet breathing break.",
        affirmation: "Progress does not need to be loud.",
        createdAt: pastTimestamp(),
        updatedAt: pastTimestamp(),
      })
  );

  await assertFails(
    bobDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("check_ins")
      .doc("2026-11-12")
      .get()
  );

  await assertFails(
    bobDb.collection("users").doc(ALICE_UID).collection("check_ins").get()
  );
});

test("Freedom Wall posts enforce owner creation, public query alignment, and controlled interactions", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();

  await assertSucceeds(
    aliceDb.collection("posts").doc("post_1").set({
      userId: ALICE_UID,
      authorName: "Alice Mindful",
      authorMeta: "@alicemindful",
      category: "PSYCHOLOGY_FACTS",
      visibility: "PUBLIC",
      bodyText: "Expressive writing helps organize emotional thoughts.",
      likeCount: 0,
      commentCount: 0,
      shareCount: 0,
      createdAt: pastTimestamp(),
      updatedAt: pastTimestamp(),
    })
  );

  // Unfiltered list rejected; visibility == PUBLIC list succeeds
  await assertFails(bobDb.collection("posts").get());
  await assertSucceeds(
    bobDb.collection("posts").where("visibility", "==", "PUBLIC").get()
  );

  // Bob cannot edit Alice's bodyText or delete Alice's post
  await assertFails(
    bobDb
      .collection("posts")
      .doc("post_1")
      .update({ bodyText: "Tampered", updatedAt: pastTimestamp() })
  );
  await assertFails(bobDb.collection("posts").doc("post_1").delete());

  // Bob can like and comment cleanly
  await assertSucceeds(
    bobDb.collection("posts").doc("post_1").collection("likes").doc(BOB_UID).set({
      userId: BOB_UID,
      createdAt: pastTimestamp(),
    })
  );
  await assertSucceeds(
    bobDb
      .collection("posts")
      .doc("post_1")
      .update({ likeCount: 1, updatedAt: pastTimestamp() })
  );
  await assertSucceeds(
    bobDb
      .collection("posts")
      .doc("post_1")
      .collection("comments")
      .doc("comment_1")
      .set({
        userId: BOB_UID,
        postId: "post_1",
        authorName: "Bob",
        text: "Great insight!",
        createdAt: pastTimestamp(),
      })
  );
  await assertSucceeds(
    bobDb
      .collection("posts")
      .doc("post_1")
      .collection("comments")
      .where("postId", "==", "post_1")
      .get()
  );
});

test("Game progress and bookmarks are owner-scoped", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();

  await assertSucceeds(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("game_progress")
      .doc("TRIVIA")
      .set({
        userId: ALICE_UID,
        gameId: "TRIVIA",
        highScore: 400,
        lastScore: 400,
        gamesPlayed: 1,
        createdAt: pastTimestamp(),
        updatedAt: pastTimestamp(),
      })
  );

  await assertFails(
    bobDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("game_progress")
      .doc("TRIVIA")
      .get()
  );
});

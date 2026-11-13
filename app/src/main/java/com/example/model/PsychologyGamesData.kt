package com.example.model

data class TriviaQuestion(
  val id: String,
  val topic: String,
  val prompt: String,
  val options: List<String>,
  val correctIndex: Int,
  val explanation: String,
)

data class FourPicsClue(
  val emoji: String,
  val caption: String,
)

data class FourPicsPuzzle(
  val id: String,
  val answer: String,
  val clues: List<FourPicsClue>,
  val hint: String,
  val psychNote: String,
)

data class WordHuntPuzzle(
  val id: String,
  val title: String,
  val gridRows: List<String>, // 8 rows x 8 uppercase chars
  val targetWords: List<String>,
)

data class MatchPair(
  val id: String,
  val term: String,
  val definition: String,
)

data class MythFactQuestion(
  val id: String,
  val statement: String,
  val isFact: Boolean,
  val explanation: String,
)

object PsychologyGamesData {

  val triviaQuestions: List<TriviaQuestion> =
    listOf(
      TriviaQuestion(
        id = "t1",
        topic = "Memory",
        prompt = "Which memory system temporarily holds and manipulates information during problem solving?",
        options = listOf("Working memory", "Procedural memory", "Iconic memory", "Semantic memory"),
        correctIndex = 0,
        explanation =
          "Working memory acts as a mental workspace for actively holding and processing information over seconds.",
      ),
      TriviaQuestion(
        id = "t2",
        topic = "Emotion Science",
        prompt = "Putting a distressing emotion into words ('naming it to tame it') is formally called:",
        options = listOf("Sensory adaptation", "Affect labeling", "Operant extinction", "Confirmation bias"),
        correctIndex = 1,
        explanation =
          "Affect labeling recruits prefrontal regulation circuits that help dampen amygdala reactivity.",
      ),
      TriviaQuestion(
        id = "t3",
        topic = "Cognitive Psychology",
        prompt = "Remembering unfinished tasks better than completed ones is known as the:",
        options = listOf("Stroop Effect", "Zeigarnik Effect", "Halo Effect", "Placebo Effect"),
        correctIndex = 1,
        explanation =
          "Bluma Zeigarnik observed that interrupted or incomplete tasks create lingering cognitive tension until closed or planned.",
      ),
      TriviaQuestion(
        id = "t4",
        topic = "Developmental",
        prompt = "According to Vygotsky, the gap between what a learner can do alone and with guidance is the:",
        options = listOf(
          "Zone of Proximal Development",
          "Sensorimotor Stage",
          "Fundamental Attribution Gap",
          "Object Permanence Window",
        ),
        correctIndex = 0,
        explanation =
          "Supportive scaffolding helps learners grow through their Zone of Proximal Development.",
      ),
      TriviaQuestion(
        id = "t5",
        topic = "Social Psychology",
        prompt = "Attributing someone else's mistake to their personality while blaming ours on the situation is:",
        options = listOf(
          "Social facilitation",
          "Fundamental attribution error",
          "Bystander effect",
          "Cognitive fluency",
        ),
        correctIndex = 1,
        explanation =
          "We tend to overestimate internal traits in others and overlook situational constraints.",
      ),
      TriviaQuestion(
        id = "t6",
        topic = "Learning & Study",
        prompt = "Which study habit yields the strongest long-term memory retention according to cognitive research?",
        options = listOf(
          "Highlighting text twice",
          "Cramming the night before",
          "Spaced active retrieval",
          "Listening while multitasking",
        ),
        correctIndex = 2,
        explanation =
          "Spacing study sessions over time and actively testing yourself strengthens memory consolidation.",
      ),
      TriviaQuestion(
        id = "t7",
        topic = "Personality",
        prompt = "Which acronym summarizes the Five-Factor Model of personality traits?",
        options = listOf("SMART", "OCEAN", "FREUD", "MASLOW"),
        correctIndex = 1,
        explanation =
          "OCEAN stands for Openness, Conscientiousness, Extraversion, Agreeableness, and Neuroticism.",
      ),
      TriviaQuestion(
        id = "t8",
        topic = "Research Ethics",
        prompt = "Which ethical principle ensures participants understand study procedures and risks before joining?",
        options = listOf("Random assignment", "Informed consent", "Double blinding", "Split-half reliability"),
        correctIndex = 1,
        explanation =
          "Informed consent protects participant autonomy, transparency, and voluntary participation.",
      ),
    )

  val fourPicsPuzzles: List<FourPicsPuzzle> =
    listOf(
      FourPicsPuzzle(
        id = "fp1",
        answer = "EMPATHY",
        clues =
          listOf(
            FourPicsClue("👂", "Active listening"),
            FourPicsClue("🤝", "Warm connection"),
            FourPicsClue("💛", "Shared feeling"),
            FourPicsClue("👟", "In another's shoes"),
          ),
        hint = "Understanding and sharing someone else's perspective.",
        psychNote = "Empathy involves both cognitive perspective-taking and emotional resonance.",
      ),
      FourPicsPuzzle(
        id = "fp2",
        answer = "MEMORY",
        clues =
          listOf(
            FourPicsClue("🧠", "Brain storage"),
            FourPicsClue("📸", "Mental snapshot"),
            FourPicsClue("🗂️", "Encoding & recall"),
            FourPicsClue("🔑", "Retrieval cue"),
          ),
        hint = "How our minds encode, store, and retrieve experiences.",
        psychNote = "Memory relies on three core stages: encoding, consolidation/storage, and retrieval.",
      ),
      FourPicsPuzzle(
        id = "fp3",
        answer = "MINDFUL",
        clues =
          listOf(
            FourPicsClue("🌬️", "Steady breath"),
            FourPicsClue("🌿", "Present moment"),
            FourPicsClue("👀", "Curious noticing"),
            FourPicsClue("🕊️", "Non-judgmental calm"),
          ),
        hint = "Paying gentle attention to the here-and-now.",
        psychNote = "Mindfulness trains sustained attention without harshly judging passing thoughts.",
      ),
      FourPicsPuzzle(
        id = "fp4",
        answer = "SLEEP",
        clues =
          listOf(
            FourPicsClue("🌙", "Circadian rhythm"),
            FourPicsClue("🛏️", "Nightly rest"),
            FourPicsClue("💭", "REM dreams"),
            FourPicsClue("🔋", "Brain recharge"),
          ),
        hint = "Essential nightly cycle for mood & memory consolidation.",
        psychNote = "Consistent sleep strengthens hippocampal memory consolidation and emotional balance.",
      ),
      FourPicsPuzzle(
        id = "fp5",
        answer = "RESILIENCE",
        clues =
          listOf(
            FourPicsClue("🌱", "Growing after rain"),
            FourPicsClue("🌊", "Adapting to waves"),
            FourPicsClue("🧗", "Bouncing back"),
            FourPicsClue("🧭", "Steady purpose"),
          ),
        hint = "The capacity to adapt and recover positively from adversity.",
        psychNote = "Resilience grows from supportive relationships, coping skills, and self-compassion.",
      ),
      FourPicsPuzzle(
        id = "fp6",
        answer = "HABIT",
        clues =
          listOf(
            FourPicsClue("🔔", "Daily cue"),
            FourPicsClue("🔁", "Repeated routine"),
            FourPicsClue("✨", "Small reward"),
            FourPicsClue("📅", "Streak momentum"),
          ),
        hint = "An automatic behavior shaped by cue, routine, and reward.",
        psychNote = "Healthy micro-habits reduce decision fatigue and build sustainable wellness routines.",
      ),
    )

  val wordHuntPuzzles: List<WordHuntPuzzle> =
    listOf(
      WordHuntPuzzle(
        id = "wh_wellness",
        title = "Mind & Wellness Grid",
        gridRows =
          listOf(
            "CALMZTPR",
            "HOPEAXEQ",
            "BRAINQAS",
            "SLEEPWCM",
            "GENTLYEI",
            "FOCUSKJL",
            "KINDJOWE",
            "GROWTHZD",
          ),
        targetWords = listOf("CALM", "HOPE", "BRAIN", "SLEEP", "FOCUS", "PEACE", "SMILE", "GROWTH"),
      ),
      WordHuntPuzzle(
        id = "wh_cognition",
        title = "Cognitive Science Grid",
        gridRows =
          listOf(
            "LEARNZMS",
            "HABITQET",
            "MINDWKMU",
            "THINKXOD",
            "RECALLRY",
            "BIASJVYN",
            "EMPATHYP",
            "GROWTHZQ",
          ),
        targetWords = listOf("LEARN", "HABIT", "MIND", "THINK", "RECALL", "MEMORY", "STUDY", "EMPATHY"),
      ),
    )

  val connectRounds: List<List<MatchPair>> =
    listOf(
      listOf(
        MatchPair("m1", "Self-Efficacy", "Belief in your ability to succeed at a specific task"),
        MatchPair("m2", "Cognitive Bias", "Systematic shortcut or pattern in human judgment"),
        MatchPair("m3", "Grounding", "Sensory technique that anchors attention to the present"),
        MatchPair("m4", "Neuroplasticity", "The brain's capacity to reorganize connections via experience"),
        MatchPair("m5", "Intrinsic Motivation", "Doing an activity for inherent enjoyment and curiosity"),
      ),
      listOf(
        MatchPair("m6", "Operant Conditioning", "Learning shaped by consequences (reinforcement/punishment)"),
        MatchPair("m7", "Affect Labeling", "Naming an emotion aloud or in writing to soothe reactivity"),
        MatchPair("m8", "Cognitive Reframing", "Examining a stressful thought from a more balanced perspective"),
        MatchPair("m9", "Circadian Rhythm", "Internal ~24-hour biological clock regulating sleep and alertness"),
        MatchPair("m10", "Prosocial Behavior", "Voluntary action intended to help or benefit others"),
      ),
    )

  val mythFactQuestions: List<MythFactQuestion> =
    listOf(
      MythFactQuestion(
        id = "mf1",
        statement = "Humans only use 10% of their brains.",
        isFact = false,
        explanation =
          "Myth! Brain imaging shows virtually every region of the brain has active roles across rest, movement, speech, and thought.",
      ),
      MythFactQuestion(
        id = "mf2",
        statement = "Consistent sleep helps consolidate newly learned study material into long-term memory.",
        isFact = true,
        explanation =
          "Fact! Slow-wave and REM sleep actively replay and stabilize neural connections formed during daytime learning.",
      ),
      MythFactQuestion(
        id = "mf3",
        statement = "Left-brained people are strictly logical while right-brained people are strictly creative.",
        isFact = false,
        explanation =
          "Myth! Both hemispheres collaborate continuously through the corpus callosum for math, language, and creativity.",
      ),
      MythFactQuestion(
        id = "mf4",
        statement = "Self-compassion is linked to higher motivation and lower procrastination than harsh self-criticism.",
        isFact = true,
        explanation =
          "Fact! Psychological research by Kristin Neff shows self-compassion lowers fear of failure so learners recover faster.",
      ),
      MythFactQuestion(
        id = "mf5",
        statement = "Venting anger aggressively is the best way to make frustration disappear.",
        isFact = false,
        explanation =
          "Myth! Catharsis research shows rehearsing aggressive venting often sustains physiological arousal; calming breathing works better.",
      ),
      MythFactQuestion(
        id = "mf6",
        statement = "Even a 10-minute brisk walk or brief time outdoors can noticeably improve mood and attention.",
        isFact = true,
        explanation =
          "Fact! Moderate movement and brief nature exposure support executive attention and stress recovery.",
      ),
    )
}

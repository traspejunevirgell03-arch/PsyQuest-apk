package com.example.model

enum class ELibraryCategory(val displayName: String) {
  INTRODUCTION("Introduction to Psychology"),
  MENTAL_HEALTH("Mental Health & Well-being"),
  HUMAN_BEHAVIOR("Human Behavior"),
  COGNITIVE("Cognitive Psychology"),
  DEVELOPMENTAL("Developmental Psychology"),
  SOCIAL("Social Psychology"),
  THEORIES("Psychological Theories"),
  RESEARCH("Research & Academic"),
  BOARD_REVIEW("Board Exam Review"),
}

data class ELibraryResource(
  val id: String,
  val title: String,
  val authorOrOrg: String,
  val sourcePlatform: String,
  val licenseLabel: String,
  val category: ELibraryCategory,
  val summary: String,
  val keyTakeaways: List<String>,
  val externalUrl: String,
  val readingTime: String,
)

object ELibraryCatalog {
  val resources: List<ELibraryResource> =
    listOf(
      ELibraryResource(
        id = "openstax_psych_2e",
        title = "Psychology 2e — Comprehensive Open Textbook",
        authorOrOrg = "Rose M. Spielman, William J. Jenkins & Marilyn D. Lovett",
        sourcePlatform = "OpenStax (Rice University)",
        licenseLabel = "CC BY 4.0",
        category = ELibraryCategory.INTRODUCTION,
        summary =
          "A peer-reviewed introductory psychology textbook covering research methods, biopsychology, sensation, learning, memory, lifespan development, personality, and wellness.",
        keyTakeaways =
          listOf(
            "Trace psychology's evolution from structuralism and functionalism to modern cognitive neuroscience.",
            "Compare experimental, correlational, and naturalistic observation designs.",
            "Connect core biopsychosocial frameworks to everyday human behavior.",
          ),
        externalUrl = "https://openstax.org/details/books/psychology-2e",
        readingTime = "Full Textbook • 16 Chapters",
      ),
      ELibraryResource(
        id = "noba_intro_psych",
        title = "Noba Project: Foundations of Psychological Science",
        authorOrOrg = "Diener Education Fund Contributors",
        sourcePlatform = "Noba Open Educational Modules",
        licenseLabel = "CC BY-NC-SA 4.0",
        category = ELibraryCategory.INTRODUCTION,
        summary =
          "Modular scholarly chapters written by leading university professors introducing scientific skepticism, ethics, and core psychological subfields.",
        keyTakeaways =
          listOf(
            "Understand empirical falsifiability and peer review in behavioral science.",
            "Explore clinical, counseling, industrial-organizational, and educational specialties.",
          ),
        externalUrl = "https://nobaproject.com/textbooks/together-the-science-of-social-psychology",
        readingTime = "Open Module Series",
      ),
      ELibraryResource(
        id = "who_doing_what_matters",
        title = "Doing What Matters in Times of Stress: Illustrated Guide",
        authorOrOrg = "World Health Organization",
        sourcePlatform = "WHO Digital Library",
        licenseLabel = "CC BY-NC-SA 3.0 IGO",
        category = ELibraryCategory.MENTAL_HEALTH,
        summary =
          "An evidence-informed self-help stress management guide introducing grounding, unhooking from difficult thoughts, acting on personal values, being kind, and making room for emotion.",
        keyTakeaways =
          listOf(
            "Practice slow breathing and sensory grounding during acute overwhelm.",
            "Notice unhelpful thought loops without letting them steer daily choices.",
            "Remember educational wellness guides complement—not replace—professional care.",
          ),
        externalUrl = "https://www.who.int/publications/i/item/9789240003927",
        readingTime = "25 min Illustrated Guide",
      ),
      ELibraryResource(
        id = "nimh_caring_mental_health",
        title = "Caring for Your Mental Health & Knowing When to Seek Help",
        authorOrOrg = "National Institute of Mental Health",
        sourcePlatform = "NIMH Health Information (Public Domain)",
        licenseLabel = "U.S. Gov Public Domain",
        category = ELibraryCategory.MENTAL_HEALTH,
        summary =
          "Clear guidance on sleep hygiene, regular physical movement, social connectedness, stress boundaries, and recognizing signs to talk with a licensed clinician.",
        keyTakeaways =
          listOf(
            "Build small daily habits around rest, hydration, movement, and connection.",
            "Recognize persistent distress signals that warrant reaching out to a healthcare professional.",
          ),
        externalUrl = "https://www.nimh.nih.gov/health/topics/caring-for-your-mental-health",
        readingTime = "10 min Guide",
      ),
      ELibraryResource(
        id = "gutenberg_james_habits",
        title = "The Principles of Psychology — Habit & Emotion",
        authorOrOrg = "William James (1890)",
        sourcePlatform = "Project Gutenberg",
        licenseLabel = "Public Domain",
        category = ELibraryCategory.HUMAN_BEHAVIOR,
        summary =
          "William James's foundational reflections on how repeated daily actions carve lasting behavioral pathways, attention control, and emotional expression.",
        keyTakeaways =
          listOf(
            "Consistent repetition lowers cognitive effort required for positive habits.",
            "Attention shapes which stimuli enter conscious awareness and habit loops.",
          ),
        externalUrl = "https://www.gutenberg.org/ebooks/57628",
        readingTime = "Classic Primary Text",
      ),
      ELibraryResource(
        id = "bccampus_learning_motivation",
        title = "Learning, Conditioning & Intrinsic Motivation",
        authorOrOrg = "Charles Stangor & Jennifer Walinga",
        sourcePlatform = "BCcampus OpenEd",
        licenseLabel = "CC BY-NC-SA 4.0",
        category = ELibraryCategory.HUMAN_BEHAVIOR,
        summary =
          "Open textbook unit examining classical conditioning, operant reinforcement schedules, observational modeling, and self-determination motivation.",
        keyTakeaways =
          listOf(
            "Distinguish positive reinforcement, negative reinforcement, and punishment.",
            "See how autonomy, competence, and relatedness sustain intrinsic motivation.",
          ),
        externalUrl = "https://opentextbc.ca/introductiontopsychology/",
        readingTime = "20 min Study Unit",
      ),
      ELibraryResource(
        id = "mit_ocw_cognition",
        title = "Cognitive Neuroscience: Memory, Attention & Executive Function",
        authorOrOrg = "Prof. John Gabrieli",
        sourcePlatform = "MIT OpenCourseWare",
        licenseLabel = "CC BY-NC-SA 4.0",
        category = ELibraryCategory.COGNITIVE,
        summary =
          "University lecture notes exploring working memory capacity, dual-process decision making, spacing effects, and prefrontal executive control.",
        keyTakeaways =
          listOf(
            "Use spaced retrieval practice rather than massed cramming for durable long-term recall.",
            "Understand cognitive load limits when multitasking during study sessions.",
          ),
        externalUrl = "https://ocw.mit.edu/courses/9-00sc-introduction-to-psychology-fall-2011/",
        readingTime = "Lecture Notes & Syllabus",
      ),
      ELibraryResource(
        id = "pmc_metacognition_study",
        title = "Retrieval Practice & Metacognitive Regulation in Learning",
        authorOrOrg = "Jeffrey D. Karpicke & Henry L. Roediger",
        sourcePlatform = "PubMed Central (PMC)",
        licenseLabel = "Open Access Article",
        category = ELibraryCategory.COGNITIVE,
        summary =
          "Peer-reviewed cognitive psychology research showing why active recall (testing yourself) outperforms passive re-reading for long-term comprehension.",
        keyTakeaways =
          listOf(
            "Self-testing strengthens retrieval routes even when recalling feels effortful.",
            "Calibration checks prevent overconfidence bias during exam preparation.",
          ),
        externalUrl = "https://pmc.ncbi.nlm.nih.gov/articles/PMC3215861/",
        readingTime = "15 min Research Read",
      ),
      ELibraryResource(
        id = "libretexts_lifespan_dev",
        title = "Lifespan Human Development: Infancy through Late Adulthood",
        authorOrOrg = "Martha Lally & Suzanne Valentine-French",
        sourcePlatform = "Social Sci LibreTexts",
        licenseLabel = "CC BY-NC-SA 4.0",
        category = ELibraryCategory.DEVELOPMENTAL,
        summary =
          "Open developmental psychology textbook examining attachment styles, Piaget's cognitive stages, Vygotsky's scaffolding, and Erikson's psychosocial milestones.",
        keyTakeaways =
          listOf(
            "Compare Piagetian stage transitions with Vygotsky's Zone of Proximal Development.",
            "Explore socioemotional selectivity and resilience across adulthood.",
          ),
        externalUrl = "https://socialsci.libretexts.org/Bookshelves/Human_Development/Lifespan_Development_-_A_Psychological_Perspective",
        readingTime = "Full Open Textbook",
      ),
      ELibraryResource(
        id = "openstax_lifespan_ch9",
        title = "Attachment, Parenting & Adolescent Identity Formation",
        authorOrOrg = "OpenStax Psychology 2e — Chapter 9",
        sourcePlatform = "OpenStax",
        licenseLabel = "CC BY 4.0",
        category = ELibraryCategory.DEVELOPMENTAL,
        summary =
          "Accessible review of Mary Ainsworth's attachment research, Baumrind's parenting styles, and Marcia's identity status framework in emerging adulthood.",
        keyTakeaways =
          listOf(
            "Understand secure vs. anxious/avoidant attachment patterns as adaptive templates.",
            "Trace identity exploration and commitment through adolescence.",
          ),
        externalUrl = "https://openstax.org/books/psychology-2e/pages/9-1-what-is-lifespan-development",
        readingTime = "18 min Chapter",
      ),
      ELibraryResource(
        id = "noba_social_cognition",
        title = "Social Cognition, Empathy & Prosocial Behavior",
        authorOrOrg = "C. Daniel Batson & Noba Editors",
        sourcePlatform = "Noba Open Educational Modules",
        licenseLabel = "CC BY-NC-SA 4.0",
        category = ELibraryCategory.SOCIAL,
        summary =
          "Evidence-based exploration of attribution biases, cognitive dissonance, social norms, conformity, and what drives everyday helping behavior.",
        keyTakeaways =
          listOf(
            "Recognize the fundamental attribution error when judging peers' behavior.",
            "See how empathic concern motivates compassionate community support.",
          ),
        externalUrl = "https://nobaproject.com/modules/helping-and-prosocial-behavior",
        readingTime = "14 min Module",
      ),
      ELibraryResource(
        id = "archive_social_psych_open",
        title = "Principles of Social Psychology (Open Edition)",
        authorOrOrg = "Dr. Rajiv Jhangiani & Dr. Hammond Tarry",
        sourcePlatform = "Internet Archive / OpenTextBC",
        licenseLabel = "CC BY-NC-SA 4.0",
        category = ELibraryCategory.SOCIAL,
        summary =
          "Openly archived social psychology reference covering self-concept, attitudes, persuasion, group dynamics, prejudice reduction, and close relationships.",
        keyTakeaways =
          listOf(
            "Compare central vs. peripheral persuasion routes in media literacy.",
            "Examine contact hypothesis strategies for reducing intergroup bias.",
          ),
        externalUrl = "https://opentextbc.ca/socialpsychology/",
        readingTime = "Open Textbook",
      ),
      ELibraryResource(
        id = "gutenberg_adler_individual",
        title = "Understanding Human Nature (Individual Psychology)",
        authorOrOrg = "Alfred Adler",
        sourcePlatform = "Project Gutenberg / Internet Archive",
        licenseLabel = "Public Domain",
        category = ELibraryCategory.THEORIES,
        summary =
          "Classic Adlerian perspective highlighting social interest, purposeful striving, encouragement, and holistic personality development.",
        keyTakeaways =
          listOf(
            "Appreciate Adler's focus on community feeling and personal agency.",
            "Contrast psychodynamic, humanistic, cognitive-behavioral, and trait models.",
          ),
        externalUrl = "https://archive.org/details/understandinghum00adle",
        readingTime = "Primary Theoretical Text",
      ),
      ELibraryResource(
        id = "oer_personality_theories",
        title = "Personality Theory in a Cultural Context: Big Five & Humanism",
        authorOrOrg = "Mark D. Kelland",
        sourcePlatform = "LibreTexts / OER Commons",
        licenseLabel = "CC BY 4.0",
        category = ELibraryCategory.THEORIES,
        summary =
          "Comprehensive open comparison of Freud, Jung, Rogers, Maslow, Bandura's social-cognitive theory, and the Five-Factor Model (OCEAN).",
        keyTakeaways =
          listOf(
            "Map the Big Five traits: Openness, Conscientiousness, Extraversion, Agreeableness, Neuroticism.",
            "Compare Carl Rogers's unconditional positive regard with Bandura's self-efficacy.",
          ),
        externalUrl = "https://socialsci.libretexts.org/Bookshelves/Psychology/Culture_and_Community/Personality_Theory_in_a_Cultural_Context_(Kelland)",
        readingTime = "25 min Theory Guide",
      ),
      ELibraryResource(
        id = "oer_research_methods",
        title = "Research Methods in Psychology (4th Edition)",
        authorOrOrg = "Rajiv S. Jhangiani, I-Chant A. Chiang, Carrie Cuttler & Dana C. Leighton",
        sourcePlatform = "KPU / Open Textbook Library",
        licenseLabel = "CC BY-NC-SA 4.0",
        category = ELibraryCategory.RESEARCH,
        summary =
          "Practical academic textbook covering hypothesis testing, validity, reliability, APA ethical principles (IRB, informed consent), and statistical power.",
        keyTakeaways =
          listOf(
            "Differentiate internal validity, external validity, and construct validity.",
            "Evaluate p-values alongside effect sizes and replication quality.",
          ),
        externalUrl = "https://kpu.pressbooks.pub/psychmethods4e/",
        readingTime = "Full Research Manual",
      ),
      ELibraryResource(
        id = "pmc_open_science_psych",
        title = "Open Science, Preregistration & Replicability in Psychology",
        authorOrOrg = "Brian A. Nosek et al.",
        sourcePlatform = "PubMed Central (PMC)",
        licenseLabel = "Open Access Article",
        category = ELibraryCategory.RESEARCH,
        summary =
          "Academic paper detailing how preregistration, open data, and collaborative replication strengthen empirical rigor in psychological research.",
        keyTakeaways =
          listOf(
            "Learn how preregistration distinguishes confirmatory tests from exploratory findings.",
            "Read primary research critically by inspecting sample sizes and confidence intervals.",
          ),
        externalUrl = "https://pmc.ncbi.nlm.nih.gov/articles/PMC6053751/",
        readingTime = "18 min Academic Paper",
      ),
      ELibraryResource(
        id = "board_review_theories_sprint",
        title = "Psychology Licensure & Board Exam High-Yield Review: Core Domains",
        authorOrOrg = "OpenStax & KPU Open Review Compendium",
        sourcePlatform = "OpenStax + Pressbooks (CC BY)",
        licenseLabel = "CC BY 4.0",
        category = ELibraryCategory.BOARD_REVIEW,
        summary =
          "Structured board-exam synthesis pairing Developmental, Abnormal, Industrial-Organizational, and Psychological Assessment high-yield concepts with practice checkpoints.",
        keyTakeaways =
          listOf(
            "Check test psychometric essentials: split-half reliability, Cronbach's alpha, content vs. criterion validity.",
            "Review classical ethical codes: beneficence, non-maleficence, confidentiality, and informed consent.",
          ),
        externalUrl = "https://openstax.org/books/psychology-2e/pages/1-review-questions",
        readingTime = "Board Review Checklist",
      ),
      ELibraryResource(
        id = "board_review_stats_assessment",
        title = "Psychological Assessment & Applied Statistics Cram Guide",
        authorOrOrg = "Danielle Navarro (Learning Statistics with jamovi)",
        sourcePlatform = "Open Textbook Library / Archive",
        licenseLabel = "CC BY-SA 4.0",
        category = ELibraryCategory.BOARD_REVIEW,
        summary =
          "Free, rigorous statistics and psychometrics primer ideal for psychology majors preparing for comprehensive or board examinations (t-tests, ANOVA, z-scores, norms).",
        keyTakeaways =
          listOf(
            "Interpret standard scores (z-scores, T-scores, percentiles) used in psychological testing.",
            "Select between parametric (t-test, ANOVA, Pearson r) and non-parametric tests accurately.",
          ),
        externalUrl = "https://www.learnstatswithjamovi.com/",
        readingTime = "Exam Prep Reference",
      ),
    )
}

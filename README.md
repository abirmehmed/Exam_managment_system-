To increment the "Exams Created" count when the "Create Exam Framework" button is clicked, you'll need to follow these steps:

1. In your `TeacherHomepageActivity`, find the `onCreate` method and initialize the `tv_exams_created_count` TextView by finding its reference:

```kotlin
private lateinit var tvExamsCreatedCount: TextView

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_teacher_homepage)

    // ... (other initialization code)

    tvExamsCreatedCount = findViewById(R.id.tv_exams_created_count)

    // ... (other initialization code)
}
```

2. In the same activity, find the click listener for the `btn_create_exam` button and add the code to increment the count:

```kotlin
btnCreateExam.setOnClickListener {
    // Code to create the exam

    // Increment the exams created count
    val currentCount = tvExamsCreatedCount.text.toString().toIntOrNull() ?: 0
    val newCount = currentCount + 1
    tvExamsCreatedCount.text = newCount.toString()
}
```

Here's what the code does:

- `val currentCount = tvExamsCreatedCount.text.toString().toIntOrNull() ?: 0`: This line retrieves the current count displayed in the `tvExamsCreatedCount` TextView. If the text is not a valid integer, it defaults to 0.
- `val newCount = currentCount + 1`: This line increments the current count by 1 to get the new count.
- `tvExamsCreatedCount.text = newCount.toString()`: This line updates the `tvExamsCreatedCount` TextView with the new count.

Make sure to replace `btnCreateExam` with the actual reference to the "Create Exam Framework" button in your code.

By adding this code, whenever the "Create Exam Framework" button is clicked, the "Exams Created" count will be incremented by 1.

Note: This code assumes that the initial count is set to 0 or a valid integer value. If the initial count is not set or is set to a non-numeric value, the code will treat it as 0 and start incrementing from there.

If you want to persist the count across app sessions or device restarts, you'll need to store the count value in a persistent storage solution like SharedPreferences, a database, or a remote server.

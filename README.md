# Exam Management System

## Description
The Exam Management System is an Android application designed to streamline the process of managing exams, students, and results. This project provides a user-friendly interface for administrators, teachers, and students to handle all aspects of exam management efficiently.

## Features
- User authentication and authorization using Firebase
- Role-based access control for administrators, teachers, and students
- Create, update, and delete exams
- Manage student information
- Record and view exam results
- Generate reports and analytics

## Installation

### Prerequisites
- Android Studio
- Java Development Kit (JDK)
- Firebase account

### Steps

1. **Clone the repository**
    ```sh
    git clone https://github.com/abirmehmed/Exam_managment_system-.git
    cd Exam_managment_system-
    ```

2. **Open the project in Android Studio**
    - Open Android Studio.
    - Select `Open an existing Android Studio project`.
    - Navigate to the cloned directory and select it.

3. **Set up Firebase**
    - Go to the [Firebase Console](https://console.firebase.google.com/).
    - Create a new project or use an existing one.
    - Add an Android app to your Firebase project.
    - Register your app with your app's package name.
    - Download the `google-services.json` file and place it in the `app` directory of your Android project.
    - Follow the instructions to add Firebase SDKs to your project (usually involves adding dependencies to your `build.gradle` files).

4. **Sync the project with Gradle files**
    - Click on `File > Sync Project with Gradle Files` in Android Studio.

5. **Run the application**
    - Connect your Android device via USB or start an Android emulator.
    - Click on the `Run` button (green play icon) in Android Studio to build and run the app.

## Usage

### Administrator
- Log in with the admin credentials.
- Create and manage exams, students, and teachers.
- Assign roles and permissions.

### Teacher
- Log in with teacher credentials.
- Create and manage their own exams.
- Enter and update student results.

### Student
- Log in with student credentials.
- View their exam schedule and results.

## Contributing
Contributions are welcome! Please follow these steps to contribute:
1. Fork the repository.
2. Create a new branch (`git checkout -b feature-branch`).
3. Make your changes.
4. Commit your changes (`git commit -m 'Add new feature'`).
5. Push to the branch (`git push origin feature-branch`).
6. Create a new Pull Request.

## License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Contact
For any questions or feedback, please contact:
- Abir Mehmed
- [Email](mailto:abirmehmed@example.com)
- [GitHub](https://github.com/abirmehmed)

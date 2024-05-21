# Exam Management System

## Description
The Exam Management System is a comprehensive platform designed to streamline the process of managing exams, students, and results. This project aims to provide a user-friendly interface for administrators, teachers, and students to handle all aspects of exam management efficiently.

## Features
- User authentication and authorization
- Role-based access control for administrators, teachers, and students
- Create, update, and delete exams
- Manage student information
- Record and view exam results
- Generate reports and analytics

## Installation

### Prerequisites
- Python 3.x
- Django
- Other dependencies listed in `requirements.txt`

### Steps
1. **Clone the repository**
    ```sh
    git clone https://github.com/abirmehmed/Exam_managment_system-.git
    cd Exam_managment_system-
    ```

2. **Create and activate a virtual environment**
    ```sh
    python -m venv env
    source env/bin/activate  # On Windows use `env\Scripts\activate`
    ```

3. **Install dependencies**
    ```sh
    pip install -r requirements.txt
    ```

4. **Run migrations**
    ```sh
    python manage.py migrate
    ```

5. **Create a superuser**
    ```sh
    python manage.py createsuperuser
    ```

6. **Start the development server**
    ```sh
    python manage.py runserver
    ```

7. **Access the application**
    Open your web browser and navigate to `http://127.0.0.1:8000/`

## Usage

### Administrator
- Log in with the superuser account.
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
- [Email](mailto:abirmehmed@gmail.com)
- [GitHub](https://github.com/abirmehmed)


# WorkWise Mobile App - Employee Portal

Welcome to the **WorkWise Mobile App**! This app serves as the **employee portal** for managing tasks and processes such as attendance, leave requests, performance goals, training, payroll, and job applications. It connects seamlessly with the **WorkWise Web App**, where administrators/employers can manage employee records, release courses, approve requests, and more.

---

## Overview

The **WorkWise Mobile App** is designed for employees to manage their work-related activities on the go. Built with **Android Studio** and powered by **Firebase**, the app ensures a seamless experience for both employees and administrators.

---

## Features

### 1. **Employee Login**
- Employees can log in with credentials created by admins via the **WorkWise Web App**.
- Credentials are stored securely in Firebase.

---

### 2. **Employee Records**
- Employees can view and update their information (except email) through the **Employee Records** section.
- Changes are saved directly to Firebase and synchronized across the system.
- Employees can also view their details and those of other employees.

---

### 3. **Attendance**
- **Mark Attendance:** Employees can toggle a switch to mark themselves as present for the day.
- **Clock Out:** Employees can record their clock-out time to log their work hours.
- Attendance records are saved in the Firebase `attendance` collection.

---

### 4. **Leave Management**
- **View Leave Balance:** Employees can check their remaining leave days.
- **Submit Leave Requests:** Employees can fill out a form with:
  - Start and end dates (using a date picker).
  - Reason for leave.
- Leave requests are sent to the admin for approval or denial.

---

### 5. **Performance Management**
- **Set Goals:** Employees can set performance goals and track their progress.
- **Submit Feedback:** Employees can provide feedback to the admin.
- **View Personal Feedback:** Employees can view feedback provided by the admin.

---

### 6. **Payroll**
- **Claim Sheet:** Employees can input monthly hours worked and overtime as a claim sheet.
- **Monthly Payslips:** Employees can view their monthly payslips by entering their login password.
- **Bonuses:** Employees can view bonuses uploaded by the admin.

---

### 7. **Training**
- Admins release training courses with videos or meeting links through the web app.
- Employees can:
  - Enroll in courses.
  - Watch training videos or access course material.

---

### 8. **Job Applications**
- Employees can view jobs posted by the admin.
- Employees can apply for jobs, and their applications are saved to the `job_applicants` collection in Firebase.

---

## Tech Stack

### **Frontend**
- Developed using **Android Studio** with **Kotlin**.
- UI follows a modern, user-friendly design with dynamic data binding from Firebase.

### **Backend**
- Developed using **Android Studio** with **Kotlin**.
- Powered by **Firebase**:
  - **Authentication**: Secure login credentials created by the admin.
  - **Firestore Database**: Stores all employee data, attendance records, leave requests, feedback, payroll data, and job applications.

---

## Getting Started

### Prerequisites
1. **Firebase Project**:
   - Ensure you have a Firebase project set up and linked to the app.
   - Download the `google-services.json` file and place it in the `app/` directory of the Android Studio project.
   
2. **Android Studio**:
   - Install the latest version of Android Studio.
   - Set the minimum SDK to **API 21** (Lollipop).

3. **GitHub Repository**:
   - Clone the repository from GitHub.

---

### Installation
1. Clone the repository:
   ```bash
   git clone <repository_url>
   cd WorkWise-Mobile-App
   ```
2. Open the project in **Android Studio**.
3. Sync the project with Gradle files.

---

### Firebase Setup
1. Create collections in Firestore:
   - `actual_employees`: Stores employee data (FullName, Role, Department, Email, Phone, etc.).
   - `attendance`: Stores daily attendance records.
   - `leave_requests`: Stores leave requests with timestamps.
   - `feedback`: Stores feedback submitted by employees.
   - `goals`: Stores employee performance goals.
   - `goals_feedback`: Stores performance feedback for employees.
   - `payroll`: Stores employee claim sheets and payslips.
   - `job_postings`: Stores job details posted by admins.
   - `job_applicants`: Stores applications for jobs.
   - `training_courses`: Stores training course details (video links, descriptions, etc.).
   
2. Enable Firebase Authentication with **Email/Password**.

---

### Running the App
1. Build and run the app on an Android emulator or physical device.
2. Log in with credentials provided by the **WorkWise Web App**.
3. Navigate through the dashboard and explore all features.

---

## Usage Flow

1. **Admin Creates Employee**:
   - Admin registers employees through the **WorkWise Web App**.
   - Credentials are sent to the employee for login.

2. **Employee Logs In**:
   - Employee logs in to the mobile app using the credentials.

3. **Dashboard Navigation**:
   - Employee can access:
     - **Employee Records**: View/update personal details.
     - **Attendance**: Mark attendance and clock out.
     - **Leave Management**: Submit leave requests and check balance.
     - **Performance**: Set goals, view feedback, and submit feedback.
     - **Payroll**: Submit claim sheets and view payslips/bonuses.
     - **Training**: Enroll in training courses and view course material.
     - **Jobs**: Apply for job postings.

---

## Team Members - CDJKY

- **Cian Brink**
- **Dylan Pather**
- **Joshua Reddy**
- **Kayur Betchu**
- **Yuvaan Pather**

---

## Firebase Collections

| Collection Name      | Purpose                                                               |
|----------------------|-----------------------------------------------------------------------|
| `actual_employees`   | Stores employee records (name, email, role, department, etc.).        |
| `attendance`         | Stores attendance records (status, clock-in/out time).                |
| `leave_requests`     | Stores leave requests (start/end date, reason).                       |
| `feedback`           | Stores feedback submitted by employees.                               |
| `goals`              | Stores performance goals for employees.                               |
| `goals_feedback`     | Stores performance feedback for employees.                            |
| `payroll`            | Stores claim sheets, payslips, and bonuses.                           |
| `job_postings`       | Stores job postings (title, description, department, etc.).           |
| `job_applicants`     | Stores applications for jobs by employees.                            |
| `training_courses`   | Stores training course details (video links, descriptions, etc.).     |

---

## Future Enhancements

- Add push notifications for real-time updates (e.g., feedback approval, leave status).
- Improve UI with additional animations and transitions.
- Implement offline support with Firebase Firestore caching.

---

## License

This project is licensed under the MIT License - see the LICENSE file for details.

---

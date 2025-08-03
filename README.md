# Long Term Database Management System (Internship Project)

A desktop-based CRUD application built using **Java Swing** and **MySQL**, developed as part of my internship at **Primetals Technologies**. It enables efficient management of steel slab production records through a user-friendly interface and secure backend database connection.

---

##  Tech Stack

- **Language:** Java (JDK 8)
- **Frontend:** Java Swing (JFrame, JTable, etc.)
- **Database:** MySQL
- **Database Connectivity:** JDBC
- **IDE:** Eclipse
- **Libraries:** JCalendar (for date selection)

---

##  Features

-  **Create**: Add new slab production records to the database.
-  **Read**: View and filter existing records with search fields and date pickers.
-  **Update**: Modify slab details using interactive pop-up forms.
-  **Delete**: Securely remove outdated or incorrect entries.
-  **Date Filter**: Use a calendar picker (JDateChooser) to view records by specific dates.
-  **Secure**: Uses `PreparedStatement` to prevent SQL injection attacks.

---

##  Application Screens

- **Home Screen**: Navigation dropdown with "CREATE", "READ", "UPDATE", "DELETE" options.
- **Create Screen**: Input slab number, heat number, shift, etc., with form validation.
- **Read Screen**: Searchable table with filter options and detailed view popup.
- **Update Screen**: Editable form to update slab and metal element data.
- **Delete Screen**: Record deletion with confirmation and rollback on failure.

---

###  Requirements

- Java JDK 8+
- Eclipse IDE
- MySQL Server & Workbench
- MySQL JDBC Connector
- JCalendar `.jar` file

---

##  Future Scope

- User role-based access control  
- REST API layer for integration with other services  
- Cloud-hosted MySQL for remote access  
- Improved error handling and validation  

---

##  Developed By

**Tanisha Sengupta**  
Intern, Primetals Technologies  

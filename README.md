# Psychic-Coders-Force-of-Punch-Project
# Database Demo

This demo consists of an implementation of the database with a single table.

Data Model:
[PunchModel.java](https://github.com/idon0019/Psychic-Coders-Force-of-Punch-Project/blob/DatabaseExample/app/src/main/java/com/example/sqlitetest/Models/PunchModel.java)

Data Helper (Controller class):
[DatabaseHelper.java](https://github.com/idon0019/Psychic-Coders-Force-of-Punch-Project/tree/DatabaseExample/app/src/main/java/com/example/sqlitetest/DBHelpers)

The data model consist of:
```java
public PunchModel(int id, int accountID, double force, long date) {
    this.id = id;
    this.accountID = accountID;
    this.force = force;
    this.date = date;
    }
```

The DataBaseHelper class consists of methods to manipulate the data and create the database/table. Methods included are:
```java 
// adding a punch by passing in a PunchModel
public boolean addPunch(PunchModel punchModel)

// getting a list of all punches in the database
public List<PunchModel> getAllPunches()

// deleting the most recent punch entry
public void removeLastPunch()
```

The [MainActivity.java](https://github.com/idon0019/Psychic-Coders-Force-of-Punch-Project/blob/DatabaseExample/app/src/main/java/com/example/sqlitetest/MainActivity.java)
file sets up some button listeners that enable adding and removing data entries from the table.



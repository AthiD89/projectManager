import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *
 * Class with main loop and methods that utilises the
 * Project and person class
 * */
public class projectManager {

  // Class array variable to store all projects
  /**
   * List of Project objects that stores all projects
   */
  static List<Project> allProjects = new ArrayList<>();

  /**
   * List of Project objects that stores all incomplete projects
   */
  static List<Project> incompleteProjects = new ArrayList<>();

  /**
   * List of Project objects that stores all overdue projects
   */
  static List<Project> overDueProjects = new ArrayList<>();

  /**
   *
   * Main loop allows user to view or add a project.
   * When a user chooses to view a project a list of projects is presented to the user.
   * When the user selects a project from the list they have the option to edit certain
   * attributes of the project. When a user chooses to add a project they will be asked
   * to input all necessary information for the creation of a Project() object.
   *
   * @param args Console Arguments*/
  public static void main(String[] args) {
    // Objects
    Scanner input = new Scanner(System.in);

    // Variables
    String menuOption = " ";

    retrieveProjects();

    while(!menuOption.equals("0")) {

      int selectedProject;

      drawLine();
      System.out.println("""
              Project Manager
              1: View All Projects
              2: View Incomplete Projects
              3: View Late Projects
              4: Search for Project
              5: Add Project
              0: Quit""");

      menuOption = input.next();
      input.nextLine();

      // View project
      if (menuOption.equals("1")) {
        // View all Tasks
        while (true) {

          while (true) {
            try {
              // Allow user ot select a task.
              drawLine();
              System.out.println("Please choose a project(Type '0'to go back):");

              for (int i = 0; i < allProjects.size(); i++) {
                if (allProjects.get(i) != null) {
                  System.out.println((i + 1) + ": " + allProjects.get(i).getName());
                }
              }
              System.out.println();
              selectedProject = Integer.parseInt(input.nextLine()) - 1;
              break;
            } catch (NumberFormatException e) {
              System.out.println("No option selected!");
            }
          }
          if(selectedProject == -1) {
            break;
          }

          processProject(input, allProjects.get(selectedProject));
        }
      } else if (menuOption.equals("2")){
        // View Incomplete Tasks
        while (true) {
          while (true) {
            try {
              int index = 1;
              drawLine();
              System.out.println("All Incomplete Projects: \n");
              System.out.println("Please choose a project(Type '0'to go back):");
              for (Project project : allProjects) {
                if (project != null && !project.getIsFinalised()) {
                  System.out.println(index + " " + project.getName());
                  incompleteProjects.add(project);
                  index++;
                }
              }
              System.out.println();
              selectedProject = Integer.parseInt(input.nextLine()) - 1;
              break;
            } catch (NumberFormatException e) {
              System.out.println("No option selected!");
            }
          }
          if (selectedProject == -1) {
            break;
          }
          processProject(input, incompleteProjects.get(selectedProject));
        }
      } else if (menuOption.equals("3")) {
        // View Overdue Tasks
        while (true) {
          while (true) {
            try {
              int index = 1;
              drawLine();
              System.out.println("All Overdue Projects: \n");
              System.out.println("Please choose a project(Type '0'to go back):");
              for (Project project : allProjects) {
                if (project != null && project.getDeadline().isBefore(LocalDate.now())) {
                    System.out.println(index + " " + project.getName());
                    overDueProjects.add(project);
                    index++;
                }
              }
              System.out.println();
              selectedProject = Integer.parseInt(input.nextLine()) - 1;
              break;
            } catch (NumberFormatException e) {
              System.out.println("No option selected!");
            }
          }
          if (selectedProject == -1) {
            break;
          }
          processProject(input, overDueProjects.get(selectedProject));
        }
      } else if (menuOption.equals("4")){
        // Search project by Name or Number
        Project searchedProject = searchProject(input, allProjects);
        if (searchedProject != null) {
          processProject(input, searchedProject);
        } else {
          System.out.println("No Project Found!");
        }

      } else if (menuOption.equals("5")) {
        // Add project
        allProjects.add(createProject());
        System.out.println("Added project: "+ allProjects.get(allProjects.size()-1).getName());
      }
    }
    input.close();
    saveProjects();
  }

  /**
   *
   * The method retrieves all the projects and there data
   * from a text file and reads it to the allProjects
   * array.
   *
   * @since version 1.00
   */
  private static void retrieveProjects() {
    // Retrieve all projects from textfile.
    Scanner fileScanner = null;
    try {
      File projectsFile = new File("storedProjects.txt");
      fileScanner = new Scanner(projectsFile);
      while (fileScanner.hasNextLine()) {
        // Split all data into array
        String[] data = fileScanner.nextLine().split(", ");
        int[] dateArr = new int[3];
        for (int i = 0; i < 3; i++) {
          dateArr[i] = Integer.parseInt(data[7].split("-")[i]);
        }
        LocalDate date = LocalDate.of(dateArr[0], dateArr[1], dateArr[2]);
        // Creating 3 person objects for Project object
        Person architect = new Person(data[8], data[9], data[10], data[11], data[12]);
        Person constructor = new Person(data[13], data[14], data[15], data[16], data[17]);
        Person customer = new Person(data[18], data[19], data[20], data[21], data[22]);
        // Use split data array to create Project
            allProjects.add(new Project(data[0], data[1], data[2], data[3], data[4],
                    Float.parseFloat(data[5]), Float.parseFloat(data[6]), date , architect,
                    constructor, customer));
            if(Boolean.parseBoolean(data[23])){
              allProjects.get(allProjects.size()-1).setFinalised(true);
            } else {
              allProjects.get(allProjects.size()-1).setFinalised(false);
            }

            if (!data[24].equals("null") ) {
              for (int x = 0; x < 3; x++) {
                dateArr[x] = Integer.parseInt(data[24].split("-")[x]);
              }
              LocalDate completionDate = LocalDate.of(dateArr[0], dateArr[1], dateArr[2]);
              allProjects.get(allProjects.size()-1).setCompletionDate(completionDate);
            }
      }
    } catch (FileNotFoundException e) {
      System.out.println("Error, retrieving projects from file.");
      e.printStackTrace();
    } catch (NullPointerException | NumberFormatException e) {
      System.out.println("Data is project file has a structure error.");
    } finally {
      if (fileScanner != null) {
        fileScanner.close();
      }
    }

  }

  /**
   *
   * The method saves all the projects and there data
   * from the allProjects array and reads it to a textfile
   *
   */
  private static void saveProjects(){
    FileWriter fileWriter = null;
    try {
      File projectsFile = new File("storedProjects.txt");
      fileWriter = new FileWriter(projectsFile);
      // Read all project data to textfile.
      for (Project Project : allProjects) {
        if (Project != null) {
          fileWriter.write(Project.getProjectNumber() + ", ");
          fileWriter.write(Project.getName() + ", ");
          fileWriter.write(Project.getBuildingType() + ", ");
          fileWriter.write(Project.getAddress() + ", ");
          fileWriter.write(Project.getErfNumber() + ", ");
          fileWriter.write(Project.getTotalProjectFees() + ", ");
          fileWriter.write(Project.getAmountPaidToDate() + ", ");
          fileWriter.write(Project.getDeadline() + ", ");
          fileWriter.write(Project.getArchitect().getJobType() + ", ");
          fileWriter.write(Project.getArchitect().getName() + ", ");
          fileWriter.write(Project.getArchitect().getTelNumber() + ", ");
          fileWriter.write(Project.getArchitect().getEmailAddress() + ", ");
          fileWriter.write(Project.getArchitect().getPhysicalAddress() + ", ");
          fileWriter.write(Project.getContractor().getJobType() + ", ");
          fileWriter.write(Project.getContractor().getName() + ", ");
          fileWriter.write(Project.getContractor().getTelNumber() + ", ");
          fileWriter.write(Project.getContractor().getEmailAddress() + ", ");
          fileWriter.write(Project.getContractor().getPhysicalAddress() + ", ");
          fileWriter.write(Project.getCustomer().getJobType() + ", ");
          fileWriter.write(Project.getCustomer().getName() + ", ");
          fileWriter.write(Project.getCustomer().getTelNumber() + ", ");
          fileWriter.write(Project.getCustomer().getEmailAddress() + ", ");
          fileWriter.write(Project.getCustomer().getPhysicalAddress() + ", ");
          fileWriter.write(Project.getIsFinalised() + ", ");
          fileWriter.write(Project.getCompletionDate() + "\n");
        }
      }
    } catch (IOException | NullPointerException e) {
      System.out.println("File error.");
    } finally {
      if (fileWriter != null) {
        try {
          fileWriter.close();
        } catch (IOException e) {
          System.out.println("Project saving error.");
        }

      }
    }

  }

  /**
   *
   * The method searches for a project by name or
   * number
   *
   * @param input Scanner to be used for input.
   * @param allProjects Array of project objects to search through
   * @return project The project found through search
   */
  private static Project searchProject(Scanner input, List<Project> allProjects) {
    System.out.println("Enter the project name or number: ");
    String userInput = input.nextLine();
    // Check if user entered project exists and return project
    for (Project project : allProjects) {
      if (project != null && (userInput.equals(project.getName())
              || userInput.equals(project.getProjectNumber()))) {
        return project;
      }
    }
    return null;
  }

  /**
   *
   * The method allows the user to edit a chosen project and
   * allows then to change a range of attributes of the
   * project
   *
   * @param input Scanner to be used for input.
   * @param project Project object to be edited
   */
  private static void processProject(Scanner input, Project project) {
    while (project != null) {
      // Allow user to edit information of the selected task.
      drawLine();
      System.out.println(project);
      drawLine();
      System.out.println("What would you like to edit on the project "
              + project.getName()
              + "\n1: Due date.\n2: Total paid to date."
              + "\n3: Number \n4: ERF Number"
              + "\n5: Building type \n6: Address"
              + "\n7: Contractors contact details."
              + "\n8: Architect contact details."
              + "\n9: Customer contact details.\n"
              + "final : Finalise project.\n0: Go back.");
      String userInput = input.next().toLowerCase();
      input.nextLine();

      switch (userInput) {
        case "1" -> project.setDeadline(changeDueDate(input));
        case "2" -> changeAmountPaid(input, project);
        case "3" -> {
          System.out.println("Enter new project number: \n");
          project.setProjectNumber(input.next());
        }
        case "4" -> {
          System.out.println("Enter new ERF number: \n");
          project.setErfNumber(input.next());
        }
        case "5" -> {
          System.out.println("Enter new Building type: \n");
          project.setBuildingType(input.next());
        }
        case "6" -> project.setAddress(input.next());
        case "7" -> changePersonContacts(input, project.getContractor());
        case "8" -> changePersonContacts(input, project.getArchitect());
        case "9" -> changePersonContacts(input, project.getCustomer());
        case "final" -> {
          project.finaliseProject();
          userInput = "0";
        }
      }
      if (userInput.equals("0")) {
        break;
      }

    }
  }

  /**
   *
   * The method allows the user to edit the contact details
   * of the contractor of the project
   *
   * @param input Scanner to be used for input.
   * @param person Person object to be edited
   */
  private static void changePersonContacts(Scanner input, Person person) {
    // Request new contact information from user and update
    // information of Person object.
    System.out.println("Enter new phone number:");
    String number = input.next();
    input.nextLine();
    System.out.println("Enter new Email address:");
    String email = input.next();
    input.nextLine();
    person.setTelNumber(number);
    person.setEmailAddress(email);
    drawLine();
    System.out.println(person.getJobType() + " details updated!");
  }

  /**
   *
   * The method allows the user to edit the amount paid
   * of the project
   *
   * @param input Scanner to be used for input.
   * @param project Project object to be edited
   */
  private static void changeAmountPaid(Scanner input, Project project) {
    // Request new Total amount from user and update
    // Project object Amount paid.
    while (true) {
      try {
        System.out.println("Please enter new total amount paid:");
        float amount = Float.parseFloat(input.nextLine());
        project.setAmountPaidToDate(amount);
        drawLine();
        System.out.println("Amount paid to date updated!");
        break;
      } catch (NullPointerException | NumberFormatException e) {
        System.out.println("Please enter number only.");
      }
    }
  }

  /**
   *
   * The method allows the user to edit the deadline
   * of the project
   *
   * @param input Scanner to be used for input.
   * @return deadline New deadline in LocalDate format.
   */
  private static LocalDate changeDueDate(Scanner input) {

    // Request new Due date from user and update
    // due date of project object.
    LocalDate deadline;
    String[] dateArr;
    String dateStr;

    while (true) {
      int year;
      int month;
      int day;

      System.out.println("Enter the project deadline(YYYY/MM/DD): ");
      dateStr = input.nextLine();
      dateStr = dateStr.replace(" ", "/");
      dateArr = dateStr.split("/");

      try {
        year = Integer.parseInt(dateArr[0]);
        month = Integer.parseInt(dateArr[1]);
        day = Integer.parseInt(dateArr[2]);
        deadline = LocalDate.of(year, month, day);
        break;
      } catch (NumberFormatException e) {
        System.out.println("Date format is incorrect.");
      }
    }
    return deadline;
  }

  /**
   *
   * The method requests all necessary information from the
   * user to create a project object.
   *
   * @return newly created Project object
   */
  public static Project createProject() {
    // Method that requests all necessary information from user to create a
    // project object.

    // Objects
    Scanner input = new Scanner(System.in);
    Person architect = null;
    Person contractor = null;
    Person customer = null;
    LocalDate deadline;

    // Variables
    String projectNumber;
    String name;
    String buildingType;
    String address;
    String erfNumber;
    float totalProjectFees;
    float amountPaidToDate;

    drawLine();
    while (true) {
      boolean exists = false;
      System.out.println("Enter the project number: ");
      projectNumber = input.nextLine();
      for (Project project : allProjects) {
        if (projectNumber.equals(project.getProjectNumber())) {
          exists = true;
          System.out.println("Project number already used.");
          break;
        } else {
          exists = false;
        }
      }
      if (!exists) {
        break;
      }
    }

    while (true) {
      boolean exists = false;
      System.out.println("Enter the project name: ");
      name = input.nextLine();

      for (Project project : allProjects) {
        if (name.equals(project.getName())) {
          exists = true;
          System.out.println("Project name already used.");
          break;
        } else {
          exists = false;
        }
      }
      if (!exists) {
        break;
      }
    }

    drawLine();
    System.out.println("Enter the building type: ");
    buildingType = input.nextLine();
    drawLine();
    System.out.println("Enter the project address: ");
    address = input.nextLine();
    drawLine();
    System.out.println("Enter the project ERF number: ");
    erfNumber = input.nextLine();
    drawLine();

    // Basic try/catch check to prevent crashing.
    while (true) {
      try {
        System.out.println("Enter the project total cost: ");
        totalProjectFees = Float.parseFloat(input.nextLine());
        drawLine();
        break;
      } catch (NumberFormatException e) {
        System.out.println("Please enter numbers only!");
      }
    }
    while (true) {
      try {
        System.out.println("Enter the amount paid to date: ");
        amountPaidToDate = Float.parseFloat(input.nextLine());
        drawLine();
        break;
      } catch (NumberFormatException e) {
        System.out.println("Please enter numbers only!");
      }
    }

    deadline = changeDueDate(input);
    drawLine();

    // for loop to build 3 Person objects an Architect, contractor and a customer.
    for(int i = 0; i < 3; i++) {
      switch (i) {
        case 0 -> {
          architect = createPerson("Architect");
        }
        case 1 -> {
          contractor = createPerson("Contractor");
        }
        case 2 -> {
          customer = createPerson("Customer");
        }
      }
    }

    // If no name is entered one is created.
    if(name.equals("")) {
      String[] splitName = customer.getName().split(" ");
      if(splitName.length != 1) {
        name = buildingType + " " + splitName[splitName.length -1];
      } else{
        name = buildingType + " " + splitName[0];
      }
    }

    return new Project(projectNumber, name, buildingType, address, erfNumber,
            totalProjectFees, amountPaidToDate, deadline, architect,
            contractor, customer);

  }

  /**
   *
   * This method allows you to create a person object.
   *
   * @param jobType Job type to assign person object.
   * @return Newly created person object.
   */
  private static Person createPerson(String jobType) {

    String personName;
    String telNumber;
    String emailAddress;
    String physicalAddress;

    Scanner personInput = new Scanner(System.in);

    System.out.println("Enter the name of the " + jobType + ": ");
    personName = personInput.nextLine();
    drawLine();
    while (true) {
      System.out.println("Enter the tel number of the " + jobType + ": ");
      telNumber = personInput.nextLine();
      telNumber = telNumber.replace(" ", "");
      telNumber = telNumber.strip();
      if (telNumber.length() == 10) {
        drawLine();
        break;
      } else {
        System.out.println("A tel number may only have 10 characters.");
      }
    }

    while (true) {
      System.out.println("Enter the email address of the " + jobType + ": ");
      emailAddress = personInput.nextLine();
      if (!emailAddress.contains("@")) {
        System.out.println("Email format incorrect. No @ symbol detected.");
      } else {
        drawLine();
        break;
      }
    }

    System.out.println("Enter the physical address of the " + jobType + ": ");
    physicalAddress = personInput.nextLine();
    drawLine();

    return new Person(jobType, personName, telNumber, emailAddress,
            physicalAddress);
  }

  /**
   * the method prints a line to the console
   */
  // Draws line to console
  public static void drawLine(){
    for(int i = 0; i < 80; i++) {
      System.out.print("-");
    }
    System.out.println();
  }

}
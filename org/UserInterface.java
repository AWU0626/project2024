import java.util.*;
import java.util.Map.Entry;
import java.time.Instant;

public class UserInterface {
	
	
	private static DataManager ds;
	private static Organization org;
	private Scanner in = new Scanner(System.in);
	
	public UserInterface(DataManager dsArgument, Organization orgArgument) {
		ds = dsArgument;
		org = orgArgument;
	}

    public void login() {
        System.out.println("Enter your login:");
        String login = in.nextLine().trim();

        System.out.println("Enter your password:");
        String password = in.nextLine().trim();
        try{
			org = ds.attemptLogin(login, password);

		} catch(Exception e){
			System.out.println("Error trying to log in. Please try again");
            while(true){
                System.out.println("Enter your login:");
                login = in.nextLine().trim();
                System.out.println("Enter your password:");
                password = in.nextLine().trim();
                try{
                    org = ds.attemptLogin(login, password);
                    if(org == null){
                        System.out.println("Login failed. Please try again");
                    }else{
                        break;
                    }
                }catch(Exception e2){
                    System.out.println("Error trying to log in. Please try again");
                }

            }
		}
		
		if (org == null) {
			System.out.println("Login failed.");
		} else {
            start(password);
        }

    }

    public void newLogin() {
        System.out.println("Enter your new login:");
        String login = in.nextLine().trim();

        System.out.println("Enter your new password:");
        String password = in.nextLine().trim();

        System.out.println("Enter your new organization name:");
        String orgName = in.nextLine().trim();

        System.out.println("Enter your new organization description:");
        String orgDesc = in.nextLine().trim();
        try{
			org = ds.newOrganization(login, password, orgName, orgDesc);
		} catch(IllegalStateException e){
            System.out.println("This organization/login already exists. Please try to create a new login.");
            return;
		} catch (IllegalArgumentException e) {
            System.out.println("It looks like you forgot a field. Remember that all fields are required.");
			return;
        } catch (Exception e) {
            System.out.println("Error communicating with the server.");
			return;
        }
		
		if (org == null) {
			System.out.println("Login failed.");
		} else {
            start(password);
        }

    }


	public void start(String password) {
                
        while (true) {
            System.out.println("\n\n");
            if (!org.getFunds().isEmpty()) {
                System.out.println("There are " + org.getFunds().size() + " funds in this organization:");
            
                int count = 1;
                for (Fund f : org.getFunds()) {
                    
                    System.out.println(count + ": " + f.getName());
                    
                    count++;
                }
                System.out.println("Enter the fund number to see more information.");
            }
            System.out.println("Enter 0 to create a new fund");
            System.out.println("Enter 'logout' to log out of this account");
            System.out.println("Or enter 'q' or 'quit' to exit");

            // 3.2 Change Password
            System.out.println("Enter 1 to change password");

            // 3.3 Update
            System.out.println("Enter 2 to update organization information");

            // 3.4 Make Donation
            System.out.println("Enter 3 to make a donation to an existing fund.");

            String choice = in.nextLine().trim();
            
            if (choice.equals("quit") || choice.equals("q")) {
                System.out.println("Good bye!");
                break;
            }
            else if (choice.equals("logout")) {
                System.out.println("Logging out... \n\n");
                org = null;
                login();
                break;
            } else if (choice.equals("1")) {
                System.out.println("Enter your current password: ");
                String currentPassword = in.nextLine().trim();

                if (currentPassword.equals(password)) {
                    try {
                        String newPassword = changePassword(password);
                        password = newPassword;

                    } catch (Exception e) {
                        System.out.println("An error occurred while updating password.");
                    }

                } else {
                    System.out.println("Password does not match. Returning to main menu.");
                }
            } else if (choice.equals("2")) {
                System.out.println("Enter your current password: ");
                String currentPassword = in.nextLine().trim();

                if (currentPassword.equals(password)) {
                    try {
                        updateOrganizationInformation();

                    } catch (Exception e) {
                        System.out.println("An error occurred while updating organization information.");
                    }

                } else {
                    System.out.println("Password does not match. Returning to main menu.");
                }
            } else if (choice.equals("3")){
                int fundNumber;
                System.out.println("Enter the number of the fund that you would like to make a donation to.");
                while(true){
                    try{
                        fundNumber = in.nextInt();
                        if(fundNumber <= 0 || fundNumber > org.getFunds().size()){
                            System.out.println("Error: Invalid Number");
                        }else{
                            break;
                        }
                    }catch(InputMismatchException e){
                        System.out.println("Error: enter a valid number.");
                        in.nextLine();
                    }
                }
                in.nextLine();
                String contributorId = "";
                while(true){
                    System.out.println("Enter the contributor id of the contributor you would make a donation through to this fund");
                    contributorId = in.nextLine().trim();
                    if(contributorId.isEmpty()){
                        System.out.println("Error: Contributor Id cannot be empty.");
                    }else{
                        break;
                    }
                }
                String contributorName = "";
                while(true){
                    try{
                        contributorName = ds.getContributorName(contributorId);
                        if(contributorName == null){
                            System.out.println("Error: The contributor id was not valid. Please provide a valid Contributor Id");
                            contributorId = in.nextLine().trim();
                        }else{
                            break;
                        }
                    }catch(Exception e){
                        System.out.println("Error: An unexpected error occured. Please try entering the contributor Id again.");
                        contributorId = in.nextLine().trim();
                    }
                }

                long donationAmount;
                while (true) {
                    System.out.println("Enter the donation amount.");
                    try {
                        donationAmount = in.nextLong();
                        if (donationAmount <= 0) {
                            System.out.println("Error: Invalid Donation Amount. Please enter a positive number.");
                        } else {
                            break;
                        }
                    } catch (InputMismatchException e) {
                        System.out.println("Error: Please enter a valid number.");
                        in.nextLine();
                    }
                }
                try{
                    makeDonation(contributorId, contributorName,fundNumber, donationAmount);
                }catch(Exception e){
                    System.out.println(e.getMessage());
                    System.out.println("Error making donation. Please re submit the donation request.");
                }
            }
            else{
                try {
                    int option = Integer.parseInt(choice);
                    if (option == 0) {
                        createFund();
                    } else if (option <= org.getFunds().size() && option > 0) {
                        OUTER:
                        while (true) {
                            System.out.println("Enter 'a' if you want to see the full list of contributions");
                            System.out.println("Enter 'b' if you want to see contributions aggregated by contributor");
                            System.out.println("Or enter 'q' or 'quit' to exit");
                            choice = in.nextLine().trim();
                            switch (choice) {
                                case "quit", "q" -> {
                                    System.out.println("Good bye!");
                                    break OUTER;
                                }
                                case "a" -> {
                                    displayFund(option);
                                    break OUTER;
                                }
                                case "b" -> {
                                    displayFundAggregates(option);
                                    break OUTER;
                                }
                                default -> System.out.println("That wasn't an option, try again");
                            }
                        }

                    } else {
                        System.out.println("Fund number is out of bounds.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input.");
                }

            }

        }           
            
    }

    // 3.2 change password
    private String changePassword(String password) {
        while (true) {
            System.out.println("Enter new password: ");
            String newPassword = in.nextLine().trim();

            System.out.println("Enter new password again: ");
            String confirmPassword = in.nextLine().trim();

            if (!newPassword.equals(confirmPassword)) {
                System.out.println("New password does not match. Returning to main menu.");
                return password;

            } else {
                try {
                    String updatedPassword = ds.changePassword(org.getId(), newPassword);
                    System.out.println("Password changed successfully.");
                    return updatedPassword;
                } catch (Exception e) {
                    System.out.println("An error occurred while changing your new password, please try again.");
                }

            }
        }
    }

    // 3.3 update org info
    private void updateOrganizationInformation() {
        while (true) {
            boolean accUpdated = false;
            String currName = org.getName();


            System.out.println("The organization's current name is: " + org.getName());
            System.out.println("Enter 1 to change the name, 2 to proceed without changing the name");
            String changeName = in.nextLine().trim();

            if (changeName.equals("1")) {
                System.out.println("Enter organization's new name: ");
                String newName = in.nextLine().trim();

                if (!newName.equals(currName)) {
                    accUpdated = true;
                    currName = newName;
                }
            }

            String currDescription = org.getDescription();
            System.out.println("The organization's current description is: " + org.getDescription());
            System.out.println("Enter 1 to change the description, 2 to proceed without changing the description");
            String changeDescription = in.nextLine().trim();
            ;

            if (changeDescription.equals("1")) {
                System.out.println("Enter organization's new description: ");
                String newDescription = in.nextLine().trim();

                if (!newDescription.equals(currDescription)) {
                    accUpdated = true;
                    currDescription = newDescription;
                }
            }

            if (accUpdated) {
                try {
                    this.org = ds.updateAccountInfo(org, currName, currDescription);
                    System.out.println("Successfully updated organization's info.");
                    break;
                } catch (Exception e){
                    System.out.println("An error has occurred while updating organization's info, please try again.");
                }
            } else {
                System.out.println("Returning to main menu.");
                break;
            }
        }
    }

    public void makeDonation(String contributorId, String contributorName, int fundNumber, long donationAmount){
        Fund f = org.getFunds().get(fundNumber - 1);
        String fundId = f.getId();
        List<Donation> allDonations = f.getDonations();
        Donation d = ds.createDonation(fundId, contributorId, contributorName, donationAmount, Instant.now().toString());
        allDonations.add(d);
        f.setDonations(allDonations);
        System.out.println("Here are all of the successful donations.");
        for(Donation donation: f.getDonations()){
            System.out.println("* " + donation.getContributorName() + ": $" + donation.getAmount() + " on " + donation.getDate());
        }

    }
	public void createFund() {

		String name = "";
		String description = "";
		long target;
		
		while(name.isEmpty()){
			System.out.print("Enter the fund name: ");
			name = in.nextLine().trim();
			if(name.isEmpty()){
				System.out.println("Fund name cannot be left blank or empty.");
			}
		}
		while(description.isEmpty()){
			System.out.print("Enter the fund description: ");
			description = in.nextLine().trim();
			if(name.isEmpty()){
				System.out.println("Fund description cannot be left blank or empty.");
			}
		}
		
		while(true){
			System.out.print("Enter the fund target: ");
			try{
				target = Long.parseLong(in.nextLine().trim());
				if(target < 0){
					System.out.println("Fund target cannot be negative.");
				}else{
					break;
				}
			} catch (NumberFormatException e){
				System.out.println("Invalid input. Please enter a number");
			}
		}


		try{
			Fund fund = ds.createFund(org.getId(), name, description, target);
			org.getFunds().add(fund);
		}catch(IllegalArgumentException e){
			if(e.getMessage().contains("origId")){
				System.out.println("[Error] Error creating fund: origId is null");
			}else if(e.getMessage().contains("name")){
				System.out.println("[Error] Error creating fund: name is null");
			}else if(e.getMessage().contains("description")){
				System.out.println("[Error] Error creating fund: description is null");
			}
		}catch(IllegalStateException e){
			System.out.println("Error has occured with the DataManager. Please try again.");
		}
	}
	public void displayFund(int fundNumber) {
        
        Fund fund = org.getFunds().get(fundNumber - 1);
        
        System.out.println("\n\n");
        System.out.println("Here is information about this fund:");
        System.out.println("Name: " + fund.getName());
        System.out.println("Description: " + fund.getDescription());
        System.out.println("Target: $" + fund.getTarget());
        
        List<Donation> donations = fund.getDonations();
        System.out.println("Number of donations: " + donations.size());
        double total = 0;
        for (Donation donation : donations) {
            System.out.println("* " + donation.getContributorName() + ": $" + donation.getAmount() + " on " + donation.getDate());
            total += donation.getAmount();
        }
        
        double percent = (total / fund.getTarget()) * 100;
        System.out.printf("Total donation amount: $%.2f (%.2f%% of target)\n", total, percent);
        
        System.out.println("Press the Enter key to go back to the listing of funds");
        in.nextLine();
        
    }
	public void displayFundAggregates(int fundNumber) {
        
        Fund fund = org.getFunds().get(fundNumber - 1);
        
        System.out.println("\n\n");
        System.out.println("Here is information about this fund:");
        System.out.println("Name: " + fund.getName());
        System.out.println("Description: " + fund.getDescription());
        System.out.println("Target: $" + fund.getTarget());
        
        Map<String, Fund.CountDonations> aggregates = fund.getDonationAggregates();
        System.out.println("Number of contributors: " + aggregates.size());
        double total = 0;
        List<Entry<String, Fund.CountDonations>> ordering = new ArrayList<>(aggregates.entrySet());
        Collections.sort(ordering, (Entry<String, Fund.CountDonations> o1, Entry<String, Fund.CountDonations> o2) -> (int)(o2.getValue().sum - o1.getValue().sum));
        for (Entry<String, Fund.CountDonations> aggregate : ordering) {
            System.out.println("* " + aggregate.getKey() + ", " + aggregate.getValue().count + " donations, $" + aggregate.getValue().sum + " total");
            total += aggregate.getValue().sum;
        }
         
        double percent = (total / fund.getTarget()) * 100;
        System.out.printf("Total donation amount: $%.2f (%.2f%% of target)\n", total, percent);
        
        System.out.println("Press the Enter key to go back to the listing of funds");
        in.nextLine();
        
    }

	public static void main(String[] args) {
        System.out.println("Enter 'a' if you want to login to an existing org");
        System.out.println("Enter 'b' if you want to create a new org");
        System.out.println("Or enter 'q' or 'quit' to exit");
        DataManager newDs = new DataManager(new WebClient("localhost", 3001));
        UserInterface ui = new UserInterface(newDs, null);
        OUTER:
            while (true) {
                String option = ui.in.nextLine().trim();
                switch (option) {
                    case "a" -> ui.login();
                    case "b" -> ui.newLogin();
                    case "q" -> {
                        break OUTER;
                }
                    default -> System.out.println("That wasn't an option. Try again.");
                }
            }
            ui.in.close();
		

        
	}


	

}

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
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
			System.out.println("Error communicating with the server.");
			return;
		}
		
		if (org == null) {
			System.out.println("Login failed.");
		} else {
            start();
        }

    }

	public void start() {
                
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
            }
            
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
		
		DataManager newDs = new DataManager(new WebClient("localhost", 3001));
		
		String login = args[0];
		String password = args[1];
		
		Organization newOrg;
		try{
			org = ds.attemptLogin(login, password);
			if (org == null) {
				System.out.println("Login failed.");
			}
			else {

				UserInterface ui = new UserInterface(ds, org);

				ui.start();

			}

		}
		catch (IllegalArgumentException e){
			System.out.println(e.getMessage());
			System.out.println("Please try to log in again with non null login and password");
		}
		catch(IllegalStateException e){
			System.out.println("Error communicating with the server. Please try again.");
			return;
		}
	}


	

}

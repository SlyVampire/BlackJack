import java.util.*;
import java.io.*;
public class Main {

	public static void main(String[] args) throws Exception {

		BlackjackGame mygame = new BlackjackGame();

		mygame.initializeGame();
		do {
			mygame.shuffle();
			mygame.getBets();
			mygame.dealCards();
			mygame.printStatus();
			mygame.checkBlackjack();
			mygame.hitOrStand();
			mygame.dealerPlays();
			mygame.settleBets();
			mygame.printMoney();
			mygame.clearHands();
		} while (mygame.playAgain());
		//mygame.endGame();
	}

}
class BlackjackGame {
	
	private Scanner ki = new Scanner(System.in);
	private int users; 
	private Player[] players;
	private Deck deck;
	private Dealer dealer = new Dealer();

	// Starts game and displays the rules
	public void initializeGame(){
		String names;
		System.out.println("Welcome to Blackjack!");
		System.out.println("");
		System.out.println("  BLACKJACK RULES: ");
		System.out.println("	-Each player is dealt 2 cards. The dealer is dealt 2 cards with one face-up and one face-down.");
		System.out.println("	-Cards are equal to their value with face cards being 10 and an Ace being 1 or 11.");
		System.out.println("	-The players cards are added up for their total.");
		System.out.println("	-Players “Hit” to gain another card from the deck. Players “Stay” to keep their current card total.");
		System.out.println("	-Dealer “Hits” until they equal or exceed 17.");
		System.out.println("	-The goal is to have a higher card total than the dealer without going over 21.");
		System.out.println("	-If the player total equals the dealer total, it is a “Push” and the hand ends."); 
		System.out.println("	-Players win their bet if they beat the dealer. Players win 1.5x their bet if they get “Blackjack” which is 21.");
		System.out.println("");
		System.out.println("");
		
		// Gets the amount of players and creates them
		do {
			System.out.print("How many people are playing (1-6)? ");
			users = ki.nextInt();
			

		} while (users > 6 || users < 0);

		players = new Player[users];
		deck = new Deck();

		// Asks for player names and assigns them
		for (int i = 0; i < users; i++) {
			System.out.print("What is player " + (i + 1) + "'s name? ");
			names = ki.next();
			players[i] = new Player();
			players[i].setName(names);
		}
	}
	
	// Shuffles the deck
	public void shuffle() throws InvalidDeckPositionException, InvalidCardSuitException, InvalidCardValueException {
		deck.shuffle();
		
	}

	// Gets the bets from the players
	public void getBets(){
		int betValue;
		
		for (int i =0; i < users; i++) {  	
			if (players[i].getBank() > 0) {
			do {
				System.out.print("How much do you want to bet " + players[i]z.getName()  + (" (1-" + players[i].getBank()) + ")? " );
				betValue = ki.nextInt();
				players[i].setBet(betValue);
			} while (!( betValue > 0 && betValue <= players[i].getBank()));
			System.out.println("");
			}

		}

	}
	
	// Deals the cards to the players and dealer
	public void dealCards(){
		for (int j = 0; j < 2; j++) {
			for (int i =0; i < users; i++) {
				if(players[i].getBank() > 0)
				{
				players[i].addCard(deck.nextCard());
				}
			}

			dealer.addCard(deck.nextCard());
		}
	}
	
	// Initial check for dealer or player Blackjack
	public void checkBlackjack(){
		//System.out.println();

		if (dealer.isBlackjack() ) {
			System.out.println("Dealer has BlackJack!");
			for (int i =0; i < users; i++) {
				if (players[i].getTotal() == 21 ) {
					System.out.println(players[i].getName() + " pushes");
					players[i].push();
				} else {
					System.out.println(players[i].getName() + " loses");
					players[i].bust();
				}	
			}
		} else {
			if (dealer.peek() ) {
				System.out.println("Dealer peeks and does not have a BlackJack");
			}

			for (int i =0; i < users; i++) {
				if (players[i].getTotal() == 21 ) {
					System.out.println(players[i].getName() + " has blackjack!");
					players[i].blackjack();
				}
			}
		}		
	}
	
	// This code takes the user commands to hit or stand
	public void hitOrStand() {
		String command;
		char c;
		for (int i = 0; i < users; i++) {
			if ( players[i].getBet() > 0 ) {
				System.out.println();
				System.out.println(players[i].getName() + " has " + players[i].getHandString());

				do {
					do {
						System.out.print(players[i].getName() + " (H)it or (S)tand? ");
						command = ki.next();
						c = command.toUpperCase().charAt(0);
					} while ( ! ( c == 'H' || c == 'S' ) );
					if ( c == 'H' ) {
						players[i].addCard(deck.nextCard());
						System.out.println(players[i].getName() + " has " + players[i].getHandString());
					}
				} while (c != 'S' && players[i].getTotal() <= 21 );
			}
		}
	}
	
	// Code for the dealer to play
	public void dealerPlays() {
		boolean isSomePlayerStillInTheGame = false;
		for (int i =0; i < users && isSomePlayerStillInTheGame == false; i++){
			if (players[i].getBet() > 0 && players[i].getTotal() <= 21 ) {
				isSomePlayerStillInTheGame = true;
			}
		}
		if (isSomePlayerStillInTheGame) {
			dealer.dealerPlay(deck);
		}
	}
	
	// This code calculates all possible outcomes and adds or removes the player bets
	public void settleBets() {
		System.out.println();

		for (int i = 0; i < users; i++) {
			if (players[i].getBet() > 0 ) {
				if( players[i].getTotal() > 21 ) {
					System.out.println(players[i].getName() + " has busted");
					players[i].bust();
				} else if ( players[i].getTotal() == dealer.calculateTotal() ) {
					System.out.println(players[i].getName() + " has pushed");
					players[i].push();
				} else if ( players[i].getTotal() < dealer.calculateTotal() && dealer.calculateTotal() <= 21 ) {
					System.out.println(players[i].getName() + " has lost");
					players[i].loss();
				} else if (players[i].getTotal() == 21) {
					System.out.println(players[i].getName() + " has won with blackjack!");
					players[i].blackjack();
				} else {
					System.out.println(players[i].getName() + " has won");
					players[i].win();
					
				}
			}
		}

	}

	// This prints the players hands
	public void printStatus() {
		for (int i = 0; i < users; i++) {
			if(players[i].getBank() > 0)
			{
			System.out.println(players[i].getName() + " has " + players[i].getHandString());
			}
		}
		System.out.println("Dealer has " + dealer.getHandString(true, true));
	}
	
	// This prints the players banks and tells the player if s/he is out of the game
	public void printMoney() {
		for (int i = 0; i < users; i++) {
			if(players[i].getBank() > 0)
			{
			System.out.println(players[i].getName() + " has " + players[i].getBank());
			}
			if(players[i].getBank() == 0)
			{
			System.out.println(players[i].getName() + " has " + players[i].getBank() + " and is out of the game.");
			players[i].removeFromGame();
			}
		}
	}

	// This code resets all hands
	public void clearHands() {
		for (int i = 0; i < users; i++) {
			players[i].clearHand();
		}
		dealer.clearHand();

	}
	
	// This decides to force the game to end when all players lose or lets players choose to keep playing or not
	public boolean playAgain() {
		String command;
		char c;
		Boolean playState = true;
		if(forceEnd()) {
			playState = false;	
		}
		else {
			do {
				System.out.println("");
				System.out.print("Do you want to play again (Y)es or (N)o? ");
				command = ki.next();
				c = command.toUpperCase().charAt(0);
			} while ( ! ( c == 'Y' || c == 'N' ) );
			if(c == 'N')
			{
				playState = false;
			}
		}
		return playState;
	}
	
	// This says true or false to forcing the game to end
	public boolean forceEnd() {
		boolean end = false;
		int endCount = 0;
		
		for (int i = 0; i < users; i++) {
			if(players[i].getBank() == -1)
			{
				endCount++;
			}
		}
		if(endCount == users)
		{
			end = true;
		}
		if(end)
		{
			System.out.println("");
			System.out.println("All players have lost and the game ends.");
		}
		
		return end;
	}
	
	// This is the endgame code for when all players are out of the game or players decide to stop playing
		public void endGame() {
			int endAmount;
			String endState = " no change.";
			System.out.println("");
			for (int i = 0; i < users; i++) {
				if(players[i].getBank() == -1)
				{
					players[i].resetBank();
				}
				endAmount = players[i].getBank() - 100;
				if(endAmount > 0)
				{
					endState = " gain of ";
				}
				else if(endAmount < 0)
				{
					endState = " loss of ";
				}
				System.out.println(players[i].getName() + " has ended the game with " + players[i].getBank() + ".");
				if(endState != " no change.")
				{
				System.out.println("A" + endState + Math.abs(endAmount) + ".");
				}
				else
				{
				System.out.println("No change from their starting value.");	
				}
				System.out.println("");
			}
			System.out.println("");
			System.out.println("");
			System.out.println("Thank you for playing!");
		}


} //End class
 class Card implements Serializable
{

	/*Making data "private" is information hiding, so that it cannot be access by
	 *someone else with code outside this class.*/
	private char suit;
	private int value;

	private Card() {

		suit = ' ';
		value = 0;


	}

	public Card(char newSuit, int newValue) throws InvalidCardValueException, InvalidCardSuitException {
		if (newValue < 1 || newValue > 13) {
			throw new InvalidCardValueException(newValue);
		} else {
			
			this.value = newValue;
		}
		if (newSuit != 'H' && newSuit != 'S' && newSuit != 'D' && newSuit != 'C') {
			throw new InvalidCardSuitException(newSuit);
		} else {
			this.suit = newSuit;
		}
		
		}


	public String toString() {
		
		return getSuitName() + " " + this.value;

	}

	public String getSuitName() {

		String suit;
		
		if (this.suit == 'H') {

			suit = "Hearts";

		}
		else if (this.suit == 'S') {

			suit = "Spades";

		}
		else if (this.suit == 'C') {

			suit = "Clubs";

		}
		else if (this.suit == 'D') {

			suit = "Diamonds";

		} else {

			suit = "Unknown";
		}
		
		return suit;

	}
	public char getSuitDesignator() {

		return suit;

	}
	public String getValueName(){

		String name = "Unknown";

		if (this.value == 1) {		
			name = "Ace";
		}
		else if (this.value == 2) {
			name = "Two";
		}
		else if (this.value == 3) {
			name = "Three";
		}
		else if (this.value == 4) {
			name = "Four";
		}
		else if (this.value == 5) {
			name = "Five";
		}
		else if (this.value == 6) {
			name = "Six";
		}
		else if (this.value == 7) {
			name = "Seven";
		}
		else if (this.value == 8) {
			name = "Eight";
		}
		else if (this.value == 9) {

			name = "Nine";
		}
		else if (this.value == 10) {

			name = "Ten";
		}
		else if (this.value == 11) {

			name = "Jack";
		}
		else if (this.value == 12) {

			name = "Queen";
		}
		else if (this.value == 13) {

			name = "King";

		} 
		return name;

	}
	
	/*This is encapsulation, it's providing access to the hidden information by
	 *putting it together in one unit with a public method. So, anyone who wants
	 *our data will have to use a setter/getter.*/
	public int getValue() {
		
		return this.value;
	}
	
	
	public boolean compareSuit(Card card){
		
		return this.suit == card.getSuitDesignator();
		
	}
	
	public boolean compareValue(Card card){
		
		return this.value == card.getValue();
	}
	
	public boolean compareTo(Card card){
		
		return this.suit == card.getSuitDesignator() && this.value == card.getValue();
	}

} //End class
 class Dealer implements Serializable
{

	private Hand hand = new Hand();

	// Determines if dealer has a blackjack
	public boolean isBlackjack(){
		if (hand.calculateTotal() == 21){
			return true;
		} else {
			return false;
		}
	}
	
	// This automates the dealer's play
	public void dealerPlay(Deck deck){
		System.out.println();
		while (hand.calculateTotal() <= 16) {
			System.out.println("Dealer has " + hand.calculateTotal()+ " and hits");
			hand.addCard(deck.nextCard());
			System.out.println("Dealer " + this.getHandString(true, false));
		} 
		if ( hand.calculateTotal() > 21) {
			System.out.println("Dealer busts. " + this.getHandString(true, false));
		} else {
			System.out.println("Dealer stands. " + this.getHandString(true, false));
		}
	}
	
	// Adds a card o the dealer's hand
	public void addCard(Card card) {
		hand.addCard(card);

	}
	
	// Gets the dealer's hand as a string
	public String getHandString(boolean isDealer, boolean hideHoleCard ) {
		String str = "Cards:" + hand.toString(isDealer, hideHoleCard);

		return str;
	}
	
	// Calculates the dealer's hand total
	public int calculateTotal() {
		return hand.calculateTotal();
	}
	
	// Clears the dealer's hand
	public void clearHand() {
		hand.clearHand();
	}
	
	// Peeks the dealer's face-down card
	public boolean peek() {
		return hand.dealerPeek();
	}
} //End class
class Deck extends Exception implements Serializable
{

	private int nextCardIndex;
	

	Card[] deck = new Card[52];

	public Deck(){

		int count = 0;
		try{
		for (int i = 1; i <= 13; i++) {
			deck[count++] = new Card('H', i);
		}
		for (int i = 1; i <= 13; i++) {
			deck[count++] = new Card('S', i);
		}
		for (int i = 1; i <= 13; i++) {
			deck[count++] = new Card('C', i);
		}
		for (int i = 1; i <= 13; i++) {
			deck[count++] = new Card('D', i);
		}	
		}
		
		catch(InvalidCardValueException | InvalidCardSuitException exp1) {
			
		}
		nextCardIndex = 0;
	}
	private void isIndexGood(int index) throws InvalidDeckPositionException {
		if (index < 0 || index > 51) {
			throw new InvalidDeckPositionException(index);
		}
	}
	
	public String toString(){

		String str = "";

		for (int i = 0; i < deck.length; i++) {
			str +=	deck[i].toString() + " ";
		}
		return str;
	}


	private void swapCards(int index1, int index2) throws InvalidDeckPositionException {	
		Card hold;

		isIndexGood(index1);
		isIndexGood(index2);
		hold = deck[index1];
		deck[index1] = deck[index2];
		deck[index2] = hold;
	}

	public void shuffle() throws InvalidDeckPositionException {
		Random rn = new Random();
		for (int i = 0; i < 4; i++){
			for (int j = 0; j < deck.length; j++) {
				swapCards(i, rn.nextInt(52));
			}
		}
		nextCardIndex = 0;
	}
	
	public Card getCard(int index) throws InvalidDeckPositionException{
		isIndexGood(index);
		return deck[index];
	}

		

	public boolean compareTo(Deck otherDeck) throws InvalidDeckPositionException {
		for (int i=0; i < deck.length; i++){
			if (! deck[i].compareTo(otherDeck.getCard(i)) ) {
				return false;
			}
		}
		return true;
	}

	public Card nextCard() {

		if (nextCardIndex < 0 || nextCardIndex > 51) {
			System.out.println("Future exception goes here");
		}
		return deck[nextCardIndex++];
	}

} //End class
class Hand implements Serializable
{

	private Card[] theHand = new Card[12];

	private int numberOfCards = 0;

	// Calculates the total of a hand and also decides whether ace is 1 or 11
	public int calculateTotal() {
		int total =0;
		boolean aceFlag = false;
		for (int i = 0; i < numberOfCards; i++) {
			int value = theHand[i].getValue();
			if (value > 10) {
				value = 10;
			} else if ( value == 1) {
				aceFlag = true;
			}
			total += value;
		}
		if (aceFlag && total + 10 <= 21) {
			total += 10;
		}
		return total;
	}
	
	public String toString(){
		return this.toString(false, false);
	}
	
	public String toString(boolean isDealer, boolean hideHoleCard){
		String str = "";
		int total =0;
		boolean aceFlag = false;
		String aceString = "";
		for (int i = 0; i < numberOfCards; i++) {
			if ( isDealer && hideHoleCard && i == 0) {
				str = " Showing";
			} else {
				int value = theHand[i].getValue();
				String valueName;
				if (value > 10) {
					valueName = theHand[i].getValueName().substring(0, 1);
				} else if ( value == 1 ){
					valueName = "A";
				} else {
					valueName = Integer.toString(value);
				}
						str += " " +valueName + theHand[i].getSuitDesignator();
				if (value > 10) {
					value = 10;
				} else if ( value == 1) {
					aceFlag = true;
				}
				total += value;
			}
		}
		if (aceFlag && total + 10 <= 21) {
			aceString = " or "+ (total + 10);
		}
		if ( hideHoleCard) {
			return str;
		} else {
			return str+ " totals "+ total + aceString;
		}
		
	}
	
	public void addCard(Card card) {
		theHand[numberOfCards++] = card;
	}
	
	public void clearHand() {
		numberOfCards = 0;
	}
	
	public boolean dealerPeek() {
		int value = theHand[1].getValue();
		return value == 1 || value >= 10;
	}
} //End class
class InvalidCardSuitException extends Exception {

	private char suitIdentifier = '?';

	public InvalidCardSuitException (char invalidSuit) {

		suitIdentifier = invalidSuit;

		System.out.println("Invalid suit" + " " + invalidSuit);
	}

	private InvalidCardSuitException() {
		System.out.println("Invalid suit");
	}
	
	public String toString(){

		return ("Attempted to create card with invalid suit argument" + " " + this.suitIdentifier);

	}
	
	public char getSuitDesignator() {
		
		return suitIdentifier;
	}
} //End class

class InvalidCardValueException extends Exception
{
	private int valueIdentifier = 0;

	public InvalidCardValueException(int invalidValue) {

		valueIdentifier = invalidValue;

		System.out.println("Invalid value " + invalidValue);
	}

	private InvalidCardValueException() {


		System.out.println("Invalid value");
	}

	public String toString() {
		

		return ("Attempted to create card with invalid suit argument" + " " + this.valueIdentifier);
	}

	public int getValue() {
		
		return valueIdentifier;
	}

} //End class
class InvalidDeckPositionException extends Exception {

	private int positionIdentifier = 0;

	public InvalidDeckPositionException(int inValidPosition) {

		positionIdentifier = inValidPosition;

		System.out.println("Invalid Position" + inValidPosition);

	}

	private InvalidDeckPositionException() {
		System.out.println("Invalid Position");
	}

	public String toString() {

		return ("Attempted to get a card from a position not in Deck" + " " + this.positionIdentifier);
	}

	public int getPositionValue() {
		return positionIdentifier;
	}
} //End class
class Player implements Serializable
{
	
	private int bank;
	private int bet;
	private String name;
	private Hand hand;
	
	// Creates a player object
	public Player() {
		bank = 100;
		hand = new Hand();
		
	}
	
	// Gets a player's bank amount
	public int getBank() {
		return bank;
	}
	
	// Removes a player's bet from their bank if they bust. Sets bet to zero afterwards.
	public void bust() {
		bank -= bet;
		bet = 0;
	}
	
	// Adds a player's bet from their bank if they win. Sets bet to zero afterwards.
	public void win() {
		bank += bet;
		bet = 0;
	}

	// Removes a player's bet from their bank if they lose. Sets bet to zero afterwards.
	public void loss() {
		bank -= bet;
		bet = 0;
	}
	
	// This sets the player bank to -1. -1 is unreachable and they are removed from the game
	public void removeFromGame() {
		bank = -1;
	}
	
	// This resets the bank to 0. Currently used to reset a removed player's bank from -1 to 0.
	public void resetBank() {
		bank = 0;
	}
	
	// This calculate the bet for a player who has a Blackjack
	public void blackjack() {
		bank += bet * 1.5;
		bet = 0;
	}
	
	// Sets a player's bet to 0 if the "push". Notice, no bet is added or removed.
	public void push() {
		bet = 0;
	}
	
	// Sets a player's bet
	public void setBet(int newBet) {
		bet = newBet;
	}
	
	// Sets a player's name
	public void setName(String name1){
		name = name1;
	}
	
	// Gets a player's name
	public String getName() {
		return name;
	}
	
	// Gets a player's hand total
	public int getTotal() {
		return hand.calculateTotal();
	}
	
	// Gets a player's bet
	public int getBet(){
		return this.bet;
	}
		
	// Adds a card to a player's hand
	public void addCard(Card card) {
		hand.addCard(card);

	}
	
	// Gets the player's cards to print as a string
	public String getHandString() {
		String str = "Cards:" + hand.toString();

		return str;
	}
		
	// Clears a player's hand
	public void clearHand() {
		hand.clearHand();
	}
		
} //End class
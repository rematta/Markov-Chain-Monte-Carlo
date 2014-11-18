import java.io.*;
import java.util.Random;
import java.util.Scanner;

public class chainprog {

	public static void main(String[] args) throws IOException{
		// TODO Auto-generated method stub
		double[][] asciitable = new double[128][128];
		FileInputStream inputStreamone = null;
		FileInputStream inputStreamtwo = null;
		String file = "";
		
		asciitable = initializetable(asciitable);
		
		Scanner scan = new Scanner(System.in);
		System.out.print("Enter a file name: ");
		file = scan.next();
		try{
		inputStreamone = new FileInputStream(file);
		} catch (IOException e){
			System.out.println("The file doesn't exist! Here the error:");
			System.out.print(e.getMessage());
			scan.close();
			return;
		}
		try{
			inputStreamtwo = new FileInputStream(file);
		} catch (IOException e){
			System.out.println("The file doesn't exist! Here the error:");
			System.out.print(e.getMessage());
			inputStreamone.close();
			scan.close();
			return;
		}

		asciitable = poptable(asciitable, inputStreamone, inputStreamtwo);
		asciitable = probtable(asciitable);
		
		//probword(asciitable);
		decrypter(asciitable);
		//printtable(asciitable);
		scan.close();
	}

	public static double[][] poptable(double[][] asciitable, FileInputStream inputStreamone, FileInputStream inputStreamtwo) throws IOException {
		int r1 = 0;
		int r2 = 0;
		r1 = inputStreamtwo.read();
		for(;;){
			if ((r1 = inputStreamone.read()) == -1)
			{
				break;
			}
			if ((r2 = inputStreamtwo.read()) == -1)
			{
				break;
			}
			
			asciitable[r1][r2]++;
			//System.out.println((char)s1+" "+(char)s2);
		}
		return asciitable;
	}
	
	public static double[][] probtable(double[][] asciitable)
	{
		int[] total = new int[128];
		for (int i = 0; i < asciitable.length; i++)
		{
			for (int j = 0; j < asciitable[0].length; j++)
			{
				total[i]+=asciitable[i][j];
			}
		}
		for (int i = 0; i < asciitable.length; i++)
		{
			if (total[i] == 0)
			{
				continue;
			}
			for (int j = 0; j < asciitable[0].length; j++)
			{
				if (asciitable[i][j]/(total[i] + asciitable.length) == 0)
				{
					continue;
				}
				asciitable[i][j] = -1 * Math.log(asciitable[i][j]/(total[i] + asciitable.length));
			}
		}
		return asciitable;
	}
	
	public static void probword(double[][] asciitable)
	{
		String word = "";
		double probability;
		Scanner scan = new Scanner(System.in);
		System.out.print("Enter a word: ");
		word = scan.next();
		probability = calculateprob(word, asciitable);
		System.out.println("The probability that the word is in the text is: "+probability);
		scan.close();
	}
	
	public static void decrypter(double[][] asciitable)
	{
		String encryptstring = "";
		double probability;
		Scanner scan = new Scanner(System.in);
		System.out.print("Enter an encrypted text to decrypt: ");
		encryptstring = scan.next();
		decrypttext(encryptstring, asciitable);
		scan.close();
	}
	
	public static void decrypttext(String estring, double[][] asciitable)
	{
		int iter = 0;
		int cont = 0;
		int rand1 = 0;
		int rand2 = 0;
		int temp = 0;
		double prob = 0;
		double probnew = 0;
		String oldstring = estring;
		String newstring = estring;
		String response = "";
		int[] matchfunction = new int[128];
		int[] newmatchfunction;
		Scanner scan = new Scanner(System.in);
		
		matchfunction = initializefunction(matchfunction);
		newmatchfunction = matchfunction.clone();
		
		//Initialization
		oldstring = convertstring(oldstring, matchfunction);
		prob = calculateprob(oldstring,asciitable);
		
		//Iteration
		while (cont == 0)
		{
			rand1 = spitrandom();
			rand2 = spitrandom();
			temp = newmatchfunction[rand1];
			newmatchfunction[rand1] = newmatchfunction[rand2];
			newmatchfunction[rand2] = temp;
			
			newstring = convertstring(newstring, newmatchfunction);
			probnew = calculateprob(newstring, asciitable);

			if (prob > probnew)
			{
				prob = probnew;
				oldstring = newstring;
				matchfunction = newmatchfunction.clone();
			}
			else 
			{
				//Condition for not switching
				if (spitrandom() > 64)
				{
					newstring = oldstring;
					newmatchfunction = matchfunction.clone();
				}
				//Condition for switching
				else
				{
					prob = probnew;
					oldstring = newstring;
					matchfunction = newmatchfunction.clone();
				}
			}
			iter++;
			if (iter % 100 == 0)
			{
				System.out.println(oldstring);
			}
			if (iter % 1000 == 0)
			{
				System.out.print("Do you want to continue decrypting? (Current Prob: "+prob+"): ");
				response = scan.next();
				response.toLowerCase();
				if (response.equals("no"))
				{
					//break out of the while loop + exit decryption
					cont = 1;
				}
			}
		}
		scan.close();
		System.out.println("The decryption process has ended.");
	}
	
	public static double calculateprob(String word, double[][] asciitable)
	{
		if (word.length() < 2)
		{
			System.out.println("The word entered is too small.");
			System.exit(0);
		}
		double probability = asciitable[(int)word.charAt(0)][(int)word.charAt(1)];
		for (int index = 1; index < word.length()-1; index++)
		{
			char indexone = word.charAt(index);
			char indextwo = word.charAt(index+1);
			probability += asciitable[indexone][indextwo];
		}
		return probability;
	}
	
	public static int spitrandom()
	{
		Random rand = new Random();
		return rand.nextInt(128-0) + 0;		
	}
	
	public static String convertstring(String estring, int[] matchfunction)
	{
		String cstring = "";
		for (int index = 0; index < estring.length(); index++)
		{
			cstring += (char)matchfunction[estring.charAt(index)];
		}
		return cstring;
	}
	
	public static void printtable(double[][] asciitable)
	{
		for (int i = 0; i < asciitable.length; i++)
		{
			System.out.println("Row "+i+":");
			for (int j = 0; j < asciitable[0].length; j++)
			{
				System.out.print(asciitable[i][j]+" ");
			}
			System.out.println("");
		}
	}
	
	public static double[][] initializetable(double[][] asciitable)
	{
		for (int indexone = 0; indexone < asciitable.length; indexone++)
		{
			for (int indextwo = 0; indextwo < asciitable[0].length; indextwo++)
			{
				asciitable[indexone][indextwo] = 1;
			}
		}
		return asciitable;
	}
	
	public static int[] initializefunction(int[] matchfunction)
	{
		for (int index = 0; index < matchfunction.length; index++)
		{
			matchfunction[index] = index;
		}
		return matchfunction;
	}
}

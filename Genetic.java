import java.util.Random;
public class Genetic
{
	//Uses a genetic algorithm to generate a string
	//Based on /r/dailyprogrammer Imtermediate Challenge #249
	public static final int POP_SIZE = 100;
	public static final boolean DEBUG = false;

	public static void main(String[] args)
	{
		if(args.length == 0)
		{
			System.out.println("Please provide a target string.");
		}
		else
		{
			String input = "";
			for(int i = 0; i<args.length;i++)
			{
				input = input + args[i];
				if(i!=args.length-1)
				{
					input = input+" ";
				}
			}
			long startTime = System.currentTimeMillis();
			generate(input);
			long endTime = System.currentTimeMillis();
			System.out.println("Execution time: " + (endTime - startTime) + " milliseconds.");
		}
	}

	public static void generate(String target)
	{
		int generation = 1;
		String father = "";
		String mother = "";
		int fatherFit = 0;
		int motherFit = 0;
		String[] population = new String[POP_SIZE];
		Random r = new Random();
		for(int i = 0; i < population.length; i++)
		{
			population[i] = firstGeneration(target.length(), r);
		}
		father = population[0];
		mother = population[1];
		fatherFit = fitness(father, target);
		motherFit = fitness(mother, target);
		for(int i = 2; i < population.length; i++)
		{
			int childFit = fitness(population[i], target);
			if(childFit > fatherFit && fatherFit < motherFit)
			{
				father = population[i];
				fatherFit = childFit;
			}
			else if(childFit > motherFit && motherFit < fatherFit)
			{
				mother = population[i];
				motherFit = childFit;
			}
		}

		if(fatherFit >= motherFit){printGen(generation, fatherFit, father);}
		else{printGen(generation, motherFit, mother);}

		while(!father.equals(target) && !mother.equals(target))
		{
			for(int i = 0; i < population.length; i++)
			{
				population[i] = mutate(splice(father, mother, r), r);
			}
			for(int i = 0; i < population.length; i++)
			{
				int childFit = fitness(population[i], target);
				//if(DEBUG){System.out.println("Child " + i + " : " + population[i] + " FIT: " + childFit);}
				if(childFit > fatherFit && fatherFit <= motherFit)
				{
					//if(DEBUG){System.out.println("Child " + i + " : " + population[i] + " TAKES FATHER.");}
					father = population[i];
					fatherFit = childFit;
				}
				else if(childFit > motherFit && motherFit <= fatherFit)
				{
					//if(DEBUG){System.out.println("Child " + i + " : " + population[i] + " TAKES MOTHER.");}
					mother = population[i];
					motherFit = childFit;
				}
			}
			generation++;
			if(fatherFit >= motherFit){printGen(generation, fatherFit, father);}
			else{printGen(generation, motherFit, mother);}
		}
	}

	public static String firstGeneration(int length, Random r)
	{
		char[] cArr = new char[length];
		for(int i = 0; i < cArr.length; i++)
		{
			cArr[i] = (char)(r.nextInt(96)+32);
		}
		return new String(cArr);
	}

	public static String splice(String father, String mother, Random r)
	{
		char[] fArr = father.toCharArray();
		char[] mArr = mother.toCharArray();
		char[] cArr = new char[mArr.length];
		for(int i = 0; i < cArr.length; i++)
		{
			int choice = r.nextInt(2);
			if(choice == 0)
			{
				cArr[i] = fArr[i];
			}
			else if(choice == 1)
			{
				cArr[i] = mArr[i];
			}
		}
		//if(DEBUG){System.out.println("SPLICED:" + new String(cArr));}
		return new String(cArr);
	}

	public static String mutate(String child, Random r)
	{
		char[] cArr = child.toCharArray();
		int invMutationRate = cArr.length;
		for(int i = 0; i < cArr.length; i++)
		{
			int choice = r.nextInt(invMutationRate);
			if(choice == 0)
			{
				cArr[i] = (char)(r.nextInt(96)+32);
			}
		}
		//if(DEBUG){System.out.println("MUTATED:" + new String(cArr));}
		return new String(cArr);
	}

	public static int customFitness(String str, String target)
	{
		//to test vs hamming fitness
		//turns out hamming fitness kicks my fitness functions ass, as i really should have known

		//each letter has a maximum of 60 points
		//20 for correct type (Letter, number, symbol)
		//10 for correct case
		//30 for exact right letter
		int score = 0;
		char[] strArr = str.toCharArray();
		char[] targetArr = target.toCharArray();
		for(int i = 0; i < strArr.length; i++)
		{
			if(strArr[i] == targetArr[i])
			{
				score = score + 50;
			}
			else if(Character.isLetter(strArr[i]) && Character.isLetter(targetArr[i]))
			{
				score = score + 20;
			}
			else if(Character.isDigit(strArr[i]) && Character.isDigit(targetArr[i]))
			{
				score = score + 20;
			}
			else if(Character.isWhitespace(strArr[i]) && Character.isWhitespace(targetArr[i]))
			{
				score = score + 20;
			}
			else if(Character.toString(strArr[i]).matches("[^A-Za-z0-9]") && Character.toString(targetArr[i]).matches("[^A-Za-z0-9]"))
			{
				score = score + 20;
			}

			if(!Character.isLetter(strArr[i]))
			{
				score = score + 10;
			}
			else
			{
				if(Character.isUpperCase(strArr[i]) == Character.isUpperCase(targetArr[i])){score = score+10;}
			}
			//if(DEBUG){System.out.println(strArr[i] + " vs " + targetArr[i] + " scores " + score);}
		}
		//if(DEBUG){System.out.println(str + " vs " + target + " scores " + score);}
		return score;
	}

	public static int fitness(String str, String target)
	{
		//tests hamming fitness
		int score = 0;
		char[] strArr = str.toCharArray();
		char[] targetArr = target.toCharArray();
		for(int i = 0; i < strArr.length; i++)
		{
			if(strArr[i] == targetArr[i]){score++;}
		}
		return score;
	}

	public static String printGen(int gen, int fit, String parent)
	{
		String str = "Gen: ";
		int pad = 3;
		str = str + padNum(gen, pad);
		str = str + " | Fitness: ";
		str = str + padNum(fit, pad);
		str = str + " | " + parent;
		System.out.println(str);
		return str;
	}

	public static String padNum(int num, int length)
	{
		String str = ""+num;
		while(str.length() < length)
		{
			str = str+" ";
		}
		return str;
	}

}
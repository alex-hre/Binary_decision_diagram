import java.util.*;

public class MainTest {
    public static void main(String[] args) {

        String function = null;
        String order = null;

        System.out.println("Enter number of variables(up to 26)");
        Scanner variablesNumberSc = new Scanner(System.in);
        int variablesNumber = variablesNumberSc.nextInt();

        System.out.println("Enter number of clauses");
        Scanner clausesNumberSc = new Scanner(System.in);
        int clausesNumber = clausesNumberSc.nextInt();

        String dnf = generateDNF(variablesNumber, clausesNumber);
            Set<String> uniqueVariables = new TreeSet<>();

            for (String clause : dnf.split("\\s*\\+\\s*")) {
                for (char varChar : clause.toCharArray()) {
                    if (Character.isLetter(varChar)) {
                        uniqueVariables.add(String.valueOf(varChar));
                    }
                }
            }

        System.out.println("DNF: " + dnf);
        System.out.println("Used variables: " + String.join("", uniqueVariables));

        function = dnf;
        order = String.join("", uniqueVariables);



        System.out.println("If you want create BDD with 'ABC' order - enter '1'\nIf BDD with best order - enter '2'");
        Scanner CreateSc = new Scanner(System.in);
        int CreateOrBestOrder = CreateSc.nextInt();

        switch (CreateOrBestOrder) {
            case (1) -> {
                Runtime runtime = Runtime.getRuntime();//mem

                // Run the garbage collector
                runtime.gc();//mem
                long c = System.currentTimeMillis();//time


                DSA_BDD bddCreate = DSA_BDD.create(function, order);


                // Print time in millis
                System.out.println("Used time in millis: " + (double) (System.currentTimeMillis() - c));//time


                // Calculate the used memory
                long memory = runtime.totalMemory() - runtime.freeMemory();
                System.out.println("Used memory is bytes: " + memory);
                System.out.println("Used memory is megabytes: "
                        + DSA_BDD.Node.bytesToMegabytes(memory));///////////DSA_BDD

                int finalSize = bddCreate.computeSize();
                int length = order.length();
                double maxSize = Math.pow(2, length + 1) - 1;
                double reduced = maxSize - finalSize;
                double percentage = 100 - (finalSize * 100 / maxSize);
                System.out.println("BDD function: " + bddCreate.getFunction());
                System.out.println("BDD order: " + bddCreate.getOrder());
                System.out.println("BDD size: " + finalSize);
                System.out.println("BDD size without reduction: " + Math.round(maxSize));
                System.out.println("BDD size of reduced nodes: " + Math.round(reduced));
                System.out.println("BDD percentage of reduced nodes: " + Math.round(percentage) + "%");


                System.out.println("If you want to use 'use' - enter '1'\nIf you want to check - enter '2'");
                Scanner UseOrCheckSc = new Scanner(System.in);
                int UseOrCheck = UseOrCheckSc.nextInt();

                switch (UseOrCheck){
                    case (1) ->{

                        System.out.println("Enter combination consisting of '1' & '2'(enter == " + length + " symbols)");
                        Scanner CombinationSc = new Scanner(System.in);
                        String Combination = CombinationSc.nextLine();

                        boolean output = bddCreate.use(Combination);

                        System.out.println("Input: " + Combination);
                        System.out.println("Output: " + output);
                    }

                    case (2) ->{

                        int counter = 0;

                        Runtime runtime1 = Runtime.getRuntime();//mem

                        // Run the garbage collector
                        runtime1.gc();//mem
                        long c1 = System.currentTimeMillis();//time



                        List<String> binaryStrings = generate(length);
                        for (String Combination : binaryStrings) {
                            //System.out.println(Combination);

                            boolean outputUse = bddCreate.use(Combination);
                            //System.out.println("Output: " + outputUse);

                            boolean outputEvaluate = DSA_BDD.Node.Evaluate(bddCreate.getRoot(), Combination, bddCreate);
                            //System.out.println("Output evaluate: " + outputEvaluate);

                            if(outputUse != outputEvaluate)
                            {
                                counter += 1;
                            }


                        }

                        if(counter != 0)
                        {
                            System.out.println("Function is incorrect");
                        }
                        else {
                            System.out.println("Function is correct");
                        }

                        // Print time in millis
                        System.out.println("Used time in millis: " + (double) (System.currentTimeMillis() - c1));//time


                        // Calculate the used memory
                        long memory1 = runtime1.totalMemory() - runtime1.freeMemory();
                        System.out.println("Used memory is bytes: " + memory1);
                        System.out.println("Used memory is megabytes: "
                                + DSA_BDD.Node.bytesToMegabytes(memory1));///////////DSA_BDD


                    }
                }



            }
            case (2) -> {
                Runtime runtime1 = Runtime.getRuntime();//mem

                // Run the garbage collector
                runtime1.gc();//mem
                long c1 = System.currentTimeMillis();//time
                DSA_BDD bddCreateBestOrder = DSA_BDD.createWithBestOrder(function);

                // Print time in millis
                System.out.println("Used time in millis: " + (double) (System.currentTimeMillis() - c1));//time


                // Calculate the used memory
                long memory1 = runtime1.totalMemory() - runtime1.freeMemory();
                System.out.println("Used memory is bytes: " + memory1);
                System.out.println("Used memory is megabytes: "
                        + DSA_BDD.Node.bytesToMegabytes(memory1));///////////DSA_BDD

                int finalSize1 = bddCreateBestOrder.computeSize();
                String bestOrder = bddCreateBestOrder.getOrder();
                int length1 = bestOrder.length();
                double maxSize1 = Math.pow(2, length1 + 1) - 1;
                double reduced1 = maxSize1 - finalSize1;
                double percentage1 = 100 - (finalSize1 * 100 / maxSize1);
                System.out.println("BDD function: " + bddCreateBestOrder.getFunction());
                System.out.println("BDD best order: " + bestOrder);
                System.out.println("BDD size: " + finalSize1);
                System.out.println("BDD size without reduction: " + Math.round(maxSize1));
                System.out.println("BDD size of reduced nodes: " + Math.round(reduced1));
                System.out.println("BDD percentage of reduced nodes: " + Math.round(percentage1) + "%");


                System.out.println("If you want to use 'use' - enter '1'\nIf you want to check - enter '2'");
                Scanner UseOrCheckSc = new Scanner(System.in);
                int UseOrCheck = UseOrCheckSc.nextInt();

                switch (UseOrCheck){
                    case (1) ->{

                        System.out.println("Enter combination consisting of '1' & '2'(enter == " + length1 + " symbols)");
                        Scanner CombinationSc = new Scanner(System.in);
                        String Combination = CombinationSc.nextLine();

                        boolean output = bddCreateBestOrder.use(Combination);

                        System.out.println("Input: " + Combination);
                        System.out.println("Output: " + output);
                    }

                    case (2) ->{

                        int counter = 0;

                        Runtime runtime2 = Runtime.getRuntime();//mem

                        // Run the garbage collector
                        runtime2.gc();//mem
                        long c2 = System.currentTimeMillis();//time



                        List<String> binaryStrings = generate(length1);
                        for (String Combination : binaryStrings) {
                            //System.out.println(Combination);

                            boolean outputUse = bddCreateBestOrder.use(Combination);
                            //System.out.println("Output: " + outputUse);

                            boolean outputEvaluate = DSA_BDD.Node.Evaluate(bddCreateBestOrder.getRoot(), Combination, bddCreateBestOrder);
                            //System.out.println("Output evaluate: " + outputEvaluate);

                            if(outputUse != outputEvaluate)
                            {
                                counter += 1;
                            }


                        }

                        if(counter != 0)
                        {
                            System.out.println("Function is incorrect");
                        }
                        else {
                            System.out.println("Function is correct");
                        }

                        // Print time in millis
                        System.out.println("Used time in millis: " + (double) (System.currentTimeMillis() - c2));//time


                        // Calculate the used memory
                        long memory2 = runtime2.totalMemory() - runtime2.freeMemory();
                        System.out.println("Used memory is bytes: " + memory2);
                        System.out.println("Used memory is megabytes: "
                                + DSA_BDD.Node.bytesToMegabytes(memory2));///////////DSA_BDD


                    }
                }


            }
        }



    }

    ///////////////////////// begin of binaryStringsCreation
    public static List<String> generate(int length) {
        List<String> result = new ArrayList<>();
        int count = (int) Math.pow(2, length);
        for (int i = 0; i < count; i++) {
            String binary = Integer.toBinaryString(i);
            while (binary.length() < length) {
                binary = "0" + binary;
            }
            result.add(binary);
        }
        return result;
    }
    ////////////////// end of binaryStringsCreation

    ////////////////// begin of generator
    public static String generateClause(int numVars) {
        Random rand = new Random();
        StringBuilder clause = new StringBuilder();

        Set<Integer> usedVars = new HashSet<>();
        int numLiterals = rand.nextInt(numVars) + 1;
        for (int i = 0; i < numLiterals; i++) {
            int varIndex;
            do {
                varIndex = rand.nextInt(numVars) + 1;
            } while (usedVars.contains(varIndex));
            usedVars.add(varIndex);

            boolean negated = rand.nextBoolean();
            char varChar = (char) ('A' + varIndex - 1);

            if (negated) {
                clause.append("!");
            }

            clause.append(varChar);
        }

        return clause.toString();
    }

    // generate  random function, that corresponds to number of clauses and variables
    public static String generateDNF(int numVars, int numClauses) {
        Set<String> uniqueClauses = new HashSet<>();

        while (uniqueClauses.size() < numClauses) {
            String newClause = generateClause(numVars);
            if (!uniqueClauses.contains(newClause)) {
                uniqueClauses.add(newClause);
            }
        }

        return String.join(" + ", uniqueClauses);
    }

    ///////////////////////////////////////////// end of generator

}

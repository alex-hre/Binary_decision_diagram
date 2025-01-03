import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DSA_BDD {
    private int computedSize = -1;
    private Node root;
    private final Node falseNode;
    private final Node trueNode;
    private final String order;
    private final Map<String, Map<String, Node>> map;

    private DSA_BDD(String function, String order, Map<String, Map<String, Node>> map) {
        root = new Node(function);
        falseNode = new Node("0");
        trueNode = new Node("1");

        this.order = order;
        this.map = map;
    }

    public Node getRoot() {
        return root;
    }

    public String getOrder() {
        return order;
    }

    public String getFunction() {
        return root.getTitleData();
    }

    public int computeSize() {
        if (computedSize == -1) {
            HashSet<DSA_BDD.Node> nodes = new HashSet<Node>();

            bringSpace(nodes, root);

            computedSize = nodes.size();
        }

        return computedSize;
    }


    public static DSA_BDD create(String function, String order) {
        if (function.isEmpty() || order.isEmpty()) {
            throw new IllegalArgumentException("Empty format");
        }

        String functionVariables = function.chars()
                .mapToObj(i -> (char) i)
                .filter(c -> c >= 'A' && c <= 'Z')
                .distinct()
                .sorted()
                .map(String::valueOf)
                .collect(Collectors.joining(""));
        String orderVariables = order.chars()
                .mapToObj(i -> (char) i)
                .sorted()
                .map(String::valueOf)
                .collect(Collectors.joining(""));

        if (!functionVariables.equals(orderVariables)) {
            throw new IllegalArgumentException("Mismatch of order and function");
        }

        if (!function.matches("[!A-Z+\\s]+")) {
            throw new IllegalArgumentException("The provided format is incorrect!");
        }

        HashMap<String, Map<String, Node>> map = new HashMap<String, Map<String, Node>>();

        for (char variable : functionVariables.toCharArray()) {
            map.put(String.valueOf(variable), new HashMap<>());
        }

        DSA_BDD bdd = new DSA_BDD(Node.format(function), order, map);

        bdd.buildTree_iReduction(bdd.getRoot(), bdd.getOrder());

        return bdd;
    }





    public static DSA_BDD createWithBestOrder(String function) {
        if (function.isEmpty()) {
            throw new IllegalArgumentException("Empty format");
        }

        String variables = function.chars()
                .mapToObj(i -> (char) i)
                .filter(c -> c >= 'A' && c <= 'Z')
                .distinct()
                .sorted()
                .map(String::valueOf)
                .collect(Collectors.joining(""));
        String temp = variables;


        HashSet<String> orders = new HashSet<String>();

        do {
            orders.add(temp);

            temp = temp + temp.charAt(0);
            temp = temp.substring(1);
        } while (!temp.equals(variables));

        List<DSA_BDD> bdds = orders.stream()
                .map(o -> create(function, o))
                .sorted(Comparator.comparing(DSA_BDD::computeSize))
                .toList();

        return bdds.get(0);
    }

    public boolean use(String input) {
        if (!input.matches("[01]+")) {
            throw new IllegalArgumentException("Incorrect format");
        }

        StringBuilder sb = new StringBuilder();
        boolean result = use(sb, root, new StringBuilder(order), input);
        //System.out.println("Used digits: " + sb.reverse());
        return result;
    }


    private boolean use(StringBuilder sb, Node root, StringBuilder order, String input) {

        if (root.equals(trueNode)) {
            return true;
        } else if (root.equals(falseNode)) {
            return false;
        }


        if (order.length() == 0 || order.length() != input.length()) {
            throw new IllegalStateException("The order is empty");
        }

        char currentOrder = order.charAt(0);
        order.deleteCharAt(0);

        if (!input.matches("[01]+")) {
            throw new IllegalArgumentException("Incorrect format");
        }

        char a = input.charAt(0);
        input = input.substring(1);

        if (root.getTitleData().chars().anyMatch(c -> c == currentOrder)) {


            if (root.getTitleData().equals(trueNode.getTitleData())) {
                return true;
            } else if (root.getTitleData().equals(falseNode.getTitleData())) {
                return false;
            } else if (root.getLeft() == null && root.getRight() == null) {
                throw new IllegalStateException("Unexpected value: " + root.getData());
            } else {
                boolean res = use(sb, a == '0' ? root.getLeft() : root.getRight(), order, input);
                sb.append(a);
                return res;
            }
        } else {
            return use(sb, root, order, input);
        }
    }

    private void buildTree_iReduction(Node root, String order) {
        if (order.isEmpty()) {
            return;
        }

        String function = root.getData();

        String left = function.replace(order.charAt(0), '0').replace(Character.toLowerCase(order.charAt(0)), '1');
        String right = function.replace(order.charAt(0), '1').replace(Character.toLowerCase(order.charAt(0)), '0');

        left = Node.prepareData(left);
        right = Node.prepareData(right);

        if (!left.equals("1") && !left.equals("0")) {

            left = Arrays.stream(left.split("\\+"))
                    .filter(clause -> clause != null && !clause.isEmpty())
                    .map(clause -> clause.chars()
                            .mapToObj(c -> (char) c)
                            .filter(c -> (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z'))
                            .distinct()
                            .sorted()
                            .map(String::valueOf)
                            .collect(Collectors.joining(""))
                    )
                    .sorted()
                    .distinct()
                    .collect(Collectors.joining("+"));
        }


        if (!right.equals("1") && !right.equals("0")) {
            right = Arrays.stream(right.split("\\+"))
                    .filter(clause -> clause != null && !clause.isEmpty())
                    .map(clause -> clause.chars()
                            .mapToObj(c -> (char) c)
                            .filter(c -> (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z'))
                            .distinct()
                            .sorted()
                            .map(String::valueOf)
                            .collect(Collectors.joining(""))
                    )
                    .sorted()
                    .distinct()
                    .collect(Collectors.joining("+"));
        }



        Map<String, DSA_BDD.Node> mapLevel = map.get(String.valueOf(order.charAt(0)));

        boolean containsLeft = mapLevel.containsKey(left);
        DSA_BDD.Node leftNode = mapLevel.getOrDefault(left, left.equals("0") ? falseNode : left.equals("1") ? trueNode : new Node(left));

        leftNode.addParent(root);
        root.setLeft(leftNode);

        mapLevel.put(left, leftNode);

        boolean containsRight = mapLevel.containsKey(right);
        DSA_BDD.Node rightNode = mapLevel.getOrDefault(right, right.equals("0") ? falseNode : right.equals("1") ? trueNode : new Node(right));

        rightNode.addParent(root);
        root.setRight(rightNode);

        mapLevel.put(right, rightNode);

        if (!left.equals("0") && !left.equals("1")) {
            if (!containsLeft) {
                buildTree_iReduction(leftNode, order.substring(1));
            } else {
                sReduction(leftNode);
            }
        }

        if (!right.equals("0") && !right.equals("1")) {
            if (!containsRight) {
                buildTree_iReduction(rightNode, order.substring(1));
            } else {
                sReduction(rightNode);
            }
        }
        sReduction(root);
    }

    private void sReduction(Node root) {
        if (root.getLeft() != null && root.getRight() != null && root.getLeft().getData().equals(root.getRight().getData())) {
            DSA_BDD.Node child = root.getLeft();

            child.removeParent(root);

            if (!root.equals(this.root)) {
                for (DSA_BDD.Node grandparent : root.getParents()) {
                    child.addParent(grandparent);

                    if (root.equals(grandparent.getLeft())) {
                        grandparent.setLeft(child);
                    }

                    if (root.equals(grandparent.getRight())) {
                        grandparent.setRight(child);
                    }
                }
            } else {
                this.root = child;
            }
        }
    }

    private void bringSpace(Set<Node> nodes, Node root) {
        if (root == null) {
            return;
        }

        nodes.add(root);

        bringSpace(nodes, root.getLeft());
        bringSpace(nodes, root.getRight());
    }

    public static class Node {

        private final String data;
        private final String TitleData;

        private Node left = null;
        private Node right = null;
        private final Set<Node> parents;

        public void removeParent(Node parent) {
            parents.remove(parent);
        }

        public String getData() {
            return data;
        }

        public Node getLeft() {
            return left;
        }

        public Node getRight() {
            return right;
        }

        private void setLeft(Node left) {
            this.left = left;
        }

        private void setRight(Node right) {
            this.right = right;
        }

        private void addParent(Node parent) {
            parents.add(parent);
        }

        public Set<Node> getParents() {
            return parents;
        }

        public String getTitleData() {
            return TitleData;
        }


        public Node(String data) {
            this.data = data;

            parents = new HashSet<>();

            Pattern pattern = Pattern.compile("([a-z])");
            java.util.regex.Matcher matcher = pattern.matcher(data);

            TitleData = matcher.replaceAll(match -> "!" + match.group().toUpperCase()).replace("+", " + ");
        }


        protected static String format(String function) {
            function = function.replaceAll("\\s+", "");

            Pattern pattern = Pattern.compile("!([A-Z])");
            java.util.regex.Matcher matcher = pattern.matcher(function);

            return matcher.replaceAll(match -> match.group().toLowerCase().substring(1));
        }


        protected static String prepareData(String input) {
            List<String> functionVariables = input.chars()
                    .mapToObj(i -> (char) i)
                    .filter(c -> c >= 'A' && c <= 'Z')
                    .distinct()
                    .sorted()
                    .map(String::valueOf)
                    .toList();

            List<String> filtered = Arrays.stream(input.split("\\+")).filter(c -> !c.contains("0")).toList();

            if (filtered.stream().anyMatch(c -> c.matches("1+"))) {
                return "1";
            }

            String cleaned = filtered.isEmpty() ? "0" : filtered.stream().distinct()
                    .collect(Collectors.joining("+"))
                    .replace("1", "");

            filtered = Arrays.stream(cleaned.split("\\+")).toList();

            for ( String variable : functionVariables) {
                if (filtered.contains(variable) && filtered.contains(variable.toLowerCase())) {
                    return "1";
                }
            }

            return cleaned;
        }


        protected static boolean Evaluate(Node root, String input, DSA_BDD bdd) {

            String tInput = input;


            String tData = root.getData();
            String tOrder = bdd.getOrder();

            tData = tData.replaceAll("\\s+", "");

            Pattern pattern = Pattern.compile("!([A-Z])");
            java.util.regex.Matcher matcher = pattern.matcher(tData);

            tData = matcher.replaceAll(match -> match.group().toLowerCase().substring(1));

            while (tOrder.length() > 0 && tInput.length() > 0) {
                char c = tOrder.charAt(0);

                char digit = tInput.charAt(0);

                if (tData.contains(Character.toString(c)) || tData.contains(Character.toString(c).toLowerCase())) {

                    if (digit == '1') {
                        tData = tData.replace(tOrder.charAt(0), '1').replace(Character.toLowerCase(tOrder.charAt(0)), '0');
                    } else {
                        tData = tData.replace(tOrder.charAt(0), '0').replace(Character.toLowerCase(tOrder.charAt(0)), '1');
                    }

                    List<String> functionVariables = tData.chars()
                            .mapToObj(i -> (char) i)
                            .filter(a -> a >= 'A' && a <= 'Z')
                            .distinct()
                            .sorted()
                            .map(String::valueOf)
                            .toList();

                    List<String> DataList = Arrays.stream(tData.split("\\+")).filter(a -> !a.contains("0")).toList();

                    if (DataList.stream().anyMatch(a -> a.matches("1+"))) {
                        return true;
                    }

                    String done = DataList.isEmpty() ? "0" : DataList.stream().distinct()
                            .collect(Collectors.joining("+"))
                            .replace("1", "");

                    DataList = Arrays.stream(done.split("\\+")).toList();

                    for (String variable : functionVariables) {
                        if (DataList.contains(variable) && DataList.contains(variable.toLowerCase())) {
                            return true;
                        }
                    }

                    tData = done;

                }

                tOrder = tOrder.substring(1);
                tInput = tInput.substring(1);

            }

            if(tData == "1")
            {
                return true;
            } else
            {
                return false;
            }

        }


        private static final long MEGABYTE = 1024L * 1024L;

        public static long bytesToMegabytes(long bytes) {
            return bytes / MEGABYTE;
        }



    }
    



    public static void main(String [] args)
    {


        String function = "A+B+C+D";////B!A+!DC///ABC+AB
        String order = "ABCD";////CAB

        DSA_BDD bdd = DSA_BDD.create(function, order);

        int finalSize = bdd.computeSize();
        int length = order.length();
        double maxSize = Math.pow(2,length+1)-1;
        double reduced = maxSize - finalSize;
        double percentage = 100 - (finalSize*100/maxSize);







        System.out.println("BDD function: " + bdd.getFunction());
        System.out.println("BDD order: " + bdd.getOrder());
        System.out.println("BDD size: " + finalSize);
        System.out.println("BDD size without reduction: " + Math.round(maxSize));
        System.out.println("BDD size of reduced nodes: " + Math.round(reduced));
        System.out.println("BDD percentage of reduced nodes: " + percentage + "%");


        String input = "0101";
        boolean output = bdd.use(input);

        System.out.println("Input: " + input);
        System.out.println("Output use : " + output);

        boolean result = Node.Evaluate(bdd.root, input, bdd);

        System.out.println("Output evaluate: " + result);




    //////////////////////////////////////////////////////////////////////





        String function2 = "!A+B+C";
        DSA_BDD bdd2 = DSA_BDD.createWithBestOrder(function2);

        System.out.println("BDD function: " + bdd2.getFunction());
        System.out.println("BDD order: " + bdd2.getOrder());
        System.out.println("BDD size: " + bdd2.computeSize());


        String input1 = "010";
        boolean output1 = bdd2.use(input1);

        System.out.println("Input: " + input1);
        System.out.println("Output: " + output1);

        boolean result1 = Node.Evaluate(bdd.root, input1, bdd2);

        System.out.println("Output evaluate: " + result1);






    }

}
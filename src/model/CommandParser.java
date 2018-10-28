package model;

import commands.CommandInitializer;
import commands.CommandNode;
import commands.TreeNode;
import commands.Node;
import java.util.*;

/**
 * @Author Yunhao Qing
 * @Author Allen Qiu
 */

public class CommandParser {
    private String language = "English";
    private String output = "";
    private VariableMap varMap;
    private ResourceBundle resources = ResourceBundle.getBundle("languages/English");
    private CommandInitializer commandInitializer;
    private Turtle t;
    private String errorMessage;

    public CommandParser(VariableMap vars, CommandInitializer command, Turtle turt){
        varMap = vars;
        commandInitializer = command;
        t = turt;
    }

    public Turtle getMyTurtle () {
        return t;
    }

    public void parse (String str) {
        parseToList(str);
    }

    public void setLanguage(ResourceBundle language) {
        commandInitializer = new CommandInitializer(language);
    }


    private List<String> parseAndCheckList(String str){
        List<String> lines = Arrays.asList(str.split("\\r?\\n"));
        List<String> cleanLines = new ArrayList<>();
        for (String line : lines){
            if (!line.trim().isEmpty() && !(line.charAt(0)=='#')){
                cleanLines.add(line);
            }
        }
        List<String> partList = new ArrayList<>();
        for (String s : cleanLines){
            partList.addAll(Arrays.asList(s.split("\\s+")));
        }
        int count1 = 0, count2 = 0;
        for (int i = 0; i < partList.size();i++){
            String s = partList.get(i);
            if (!(isNumeric(s) || isPossibleCommand(s)|| isPossibleVariable(s) || s.equals("[") || s.equals("]"))){
                errorMessage = "Invalid input : Input contains index component at index " + i + " of the commands.";
            }
            else if (s.equals("[")) count1++;
            else if (s.equals("]")) count2++;
            if (count2 > count1){
                errorMessage = "Invalid input : More ']' than '[' at index " + i + " of the commands.";
            }
        }
        if (count1 > count2){
            errorMessage = "Invalid input : More '[' than ']', brackets cannot match.";
        }
        return cleanLines;
    }

    private List<List<String>> parseToList(String str){
        List<String> lines = parseAndCheckList(str);
        str = str.replace("\n", " ");
        List<List<String>> list = new ArrayList<>();
        int count = 0;
        while (count < lines.size()){
            String line = lines.get(count);
            List<String> temp = new ArrayList<>(Arrays.asList(line.split("\\s+")));
            while (notBalance(temp)){
                count++;
                String newline = lines.get(count);
                temp.addAll(Arrays.asList(newline.split("\\s+")));
            }
            list.add(temp);
            count++;
        }

        for (int i = 0; i < list.size();i++){
            List<String> temp = list.get(i);
            List<String> sub = new ArrayList<>();
            count = 0;
            while (count < temp.size()){
                if (!temp.get(count).equals("[") && !temp.get(count).equals("]")){
                    sub.add(temp.get(count));
                    count++;
                }
                else if (temp.get(count).equals("[")){
                    List<String> record = new ArrayList<>();
                    String recordStr = "";
                    record.add("[");
                    count++;
                    while (notBalance(record)){
                        recordStr += temp.get(count) + " ";
                        record.add(temp.get(count));
                        count++;
                    }
                    recordStr = recordStr.substring(0,recordStr.length()-3);
                    sub.add(recordStr);
                }
            }
            list.set(i,sub);
        }

        for(List<String> line:list){
            output = output + parseLine(line) + "\n";
        }
        return list;
    }


    private String parseLine(List<String> thisLine){
        String output = "";
        ArrayList<TreeNode> rootNodes = new ArrayList<>();
        //any parent must be a command
        TreeNode parent = null;
        for(String s:thisLine){
            if(isPossibleCommand(s)){
                if (!isCommand(s))
                    errorMessage = "Invalid input : " + s + " is not a valid command";
                List<String> thisValue = new ArrayList<>();
                thisValue.add(s);
                TreeNode thisCommandNode = new TreeNode(parent, thisValue);
                if(parent == null){
                    rootNodes.add(thisCommandNode);
                }
                else {
                    parent.addChild(thisCommandNode);
                }
                parent = thisCommandNode;
            }
            else if(isNumeric(s)){
                List<String> thisValue = new ArrayList<>();
                thisValue.add(s);
                Node thisNode = new Node(parent, thisValue);
                parent.addChild(thisNode);
            }
            else if(isList(s)){
                List<String> thisValue = new ArrayList<>();
                thisValue.add(s);
                Node thisNode = new Node(parent, thisValue);
                parent.addChild(thisNode);
            }
            else {
                //variable
                if(parent.getCommandName().compareToIgnoreCase(resources.getString("MakeVariable").split("\\|")[0]) == 0
                        || parent.getCommandName().compareToIgnoreCase(resources.getString("MakeVariable").split("\\|")[1]) == 0 || parent.getCommandName().compareToIgnoreCase(resources.getString("MakeUserInstruction")) == 0){
                    List<String> thisValue = new ArrayList<>();
                    thisValue.add(s);
                    Node thisNode = new Node(parent, thisValue);
                    parent.addChild(thisNode);
                }
                else {
                    //not a make command
                    if(isVariable(s)){
                        double variableValue = varMap.getVariable(s);
                        List<String> thisValue = new ArrayList<>();
                        thisValue.add(Double.toString(variableValue));
                        Node thisNode = new Node(parent, thisValue);
                        parent.addChild(thisNode);
                    }
                    else {
                            errorMessage = "Invalid input : " + s + " is not a valid variable";
                    }
                }
            }

            //check if this node fulfills the parent
            if(parent != null){
                while(parent != null && parent.fulfilled(commandInitializer)){
                    CommandNode parentCommandNode = commandInitializer.getCommandNode(parent.getCommandName());
                    double returnValue = parentCommandNode.run(mergeParameters(parent), t, varMap, commandInitializer);
                    TreeNode parentOfParent = parent.getParent();
                    if(parentOfParent != null){
                        parentOfParent.getChildren().remove(parent);
                        List<String> returnValueList = new ArrayList<>();
                        returnValueList.add(Double.toString(returnValue));
                        parentOfParent.getChildren().add(new Node(parentOfParent, returnValueList));
                    }
                    else {
                        output = output + returnValue + " ";
                    }
                    parent = parentOfParent;
                }
            }
        }
        return output;
    }

    public List<String> mergeParameters(TreeNode parent){
        List<String> mergedParameters = new ArrayList<>();
        for(Node n:parent.getChildren()){
            mergedParameters.addAll(n.getParameters());
        }
        return mergedParameters;
    }

    public String returnError(){
        return errorMessage;
    }

    public String getOutput(){
        System.out.println(output);
        return output;
    }

    private static boolean isNumeric(String str) {
        return str.matches("-?[0-9]+\\.?[0-9]*");
    }

    private static boolean isPossibleVariable(String str){
        return str.matches(":[a-zA-Z_]+");
    }

    private static boolean isPossibleCommand(String str){
        return str.matches("[a-zA-Z_]+(\\?)?");
    }

    private boolean isCommand(String s){
        return commandInitializer.containsKey(s);
    }

    private boolean isVariable(String s){
        return varMap.contains(s);
    }

    private boolean isList(String s){
        return s.split("\\s+").length > 1;
    }

    private boolean notBalance(List<String> strings){
        int count1 = 0,count2 = 0;
        for (String str : strings){
            if (str.equals("[")) count1++;
            else if (str.equals("]")) count2++;
        }
        return count1 == count2;
    }

}
//package com.troystopera.gencode;
//
//import com.google.gson.*;
//import com.troystopera.gencode.generator.CodeGenerator;
//import com.troystopera.jkode.exec.Executor;
//import com.troystopera.jkode.exec.Output;
//import com.troystopera.jkode.exec.override.OverrideExecutor;
//import com.troystopera.jkode.format.JavaFormat;
//import com.troystopera.gencode.ProblemTopic;
//import com.troystopera.jkode.JFunction;
//import com.troystopera.jkode.vars.JVar;
//
//import java.util.*;
//
///**
// * Created by troy on 8/1/17.
// */
//public class Main {
//
//    // When calling from main we print to console
//    public static void main(String... args) {
//        if (args.length < 4) {
//            System.out.println("Please provide difficulty range, number of questions, and at least one topic");
//            System.exit(-1);
//        }
//
//        double difficultyLow = Double.valueOf(args[0]);
//        double difficultyHigh = Double.valueOf(args[1]);
//        int count = Integer.valueOf(args[2]);
//        ProblemTopic[] topics = new ProblemTopic[args.length - 3];
//
//        for (int i = 3; i < args.length; i++)
//            topics[i - 3] = ProblemTopic.valueOf(args[i]);
//
//        System.out.println("Topics: " + Arrays.toString(topics));
//        System.out.println("Difficulty: " + difficultyLow + " -> " + difficultyHigh);
//        System.out.println("-------\n");
//
//        CodeGenerator generator = new CodeGenerator(topics);
//        int problemNum = 1;
//        for (Problem problem : generator.generate(difficultyLow, difficultyHigh, count)) {
//            System.out.println("Problem: " + problemNum);
//            System.out.println(JavaFormat.INSTANCE.formatFunction(problem.getMainFunction(), ""));
//
//            Executor exec = new Executor();
//            Output output = exec.execute(problem.getMainFunction());
//            JVar answer = output.getReturnVar();
//            // get rid of this:
//            if (answer != null) {
//                System.out.println("Answer: " + answer.toString());
//            } else {
//                System.out.println("Could not find answer for this problem");
//            }
//            System.out.println("\n\n");
//            problemNum++;
//        }
//    }
//
///* package com.troystopera.gencode;
//
//import com.troystopera.gencode.generator.CodeGenerator;
//import com.troystopera.gencode.generator.distractors.DistractorGenerator;
//import com.troystopera.jkode.exec.Executor;
//import com.troystopera.jkode.exec.Output;
//import com.troystopera.jkode.format.JavaFormat;
//
//import java.lang.*;
//import java.util.*;
//
///**
// * Created by troy on 8/1/17.
// */
///* public class Main {
//
//    public static void main(String... args) {
//        if (args.length < 4) {
//            System.out.println("Please provide difficulty range, number of questions, and at least one topic");
//            System.exit(-1);
//        }
//
//        double difficultyLow = Double.valueOf(args[0]);
//        double difficultyHigh = Double.valueOf(args[1]);
//        int count = Integer.valueOf(args[2]);
//        ProblemTopic[] topics = new ProblemTopic[args.length - 3];
//
//        for (int i = 3; i < args.length; i++)
//            topics[i - 3] = ProblemTopic.valueOf(args[i]);
//
//        System.out.println("Topics: " + Arrays.toString(topics));
//        System.out.println("Difficulty: " + difficultyLow + " -> " + difficultyHigh);
//        System.out.println("-------\n");
//
//        CodeGenerator generator = new CodeGenerator(topics);
//
//        int problemNum = 1;
//        for (Problem problem : generator.generate(difficultyLow, difficultyHigh, count)) {
//            System.out.println("Problem: " + problemNum);
//            System.out.println(JavaFormat.INSTANCE.formatFunction(problem.getMainFunction(), ""));
//
//            Executor exec = new Executor();
//            Output output = exec.execute(problem.getMainFunction());
//            String returnVal = output.getReturnVar().toString();
//            /* DistractorGenerator dist = new DistractorGenerator(problem);
//            List<String> distractors = dist.getDistractors(3);
//
//            // generate a random number from 0-3 to choose which spot the correct answer goes in list
//            int rand = (int) (Math.random() * 4);
//            count = 0;
//            String returnVal = output.getReturnVar().toString();
//            System.out.print("Answer choices: ");
//            for (String s: distractors) {
//                if (count == rand) {
//                    System.out.print(returnVal + " ");
//                }
//                System.out.print(s + " ");
//                count++;
//            }
//            if (rand == 3) {
//                System.out.print(returnVal);
//            } */
//            /* System.out.println("\nCorrect answer: " + returnVal);
//            System.out.println("\n\n");
//            problemNum++;
//        }
//    } */
//
//    // Displays the view for remind.
//    public static String getView(String allViews){
//        // We need to construct a jsonObject for our next view.
//        JsonObject nextViewToBeDisplayed = new JsonObject();
//
//        // Track some flags as to not step over ourselves.
//        Boolean creatingView = true;
//        JsonObject allViewsAsJson = new JsonParser().parse(allViews).getAsJsonObject();
//        JsonArray viewKeysJson = allViewsAsJson.getAsJsonArray("viewKeys");
//        String viewName;
//        // First we check if the view is start.
//        if(viewKeysJson.size() == 0){
//            System.out.println("In start creation");
//            // We need to display the topics available.
//            viewName = "start";
//            viewKeysJson.add(viewName);
//            // Create parameter for checkbox list and add it to view.
//            String guiParam = "'list': [{'value':'forLoops', 'title':'For Loops'},{'value':'conditional', 'title':'Conditional'}, {'value': 'array', 'title':'Array'}, {'value':'array_2d', 'title':'Two Dimensional Array'}]";
//            String subtitle = "Please select a combination of topics to generate questions from.";
//            nextViewToBeDisplayed = createAndAddParameterToView(nextViewToBeDisplayed, "topics", "checkbox", guiParam, "Topics", subtitle);
//
//            // We need to ask for  Difficulty range
//            subtitle = "Please input your difficulty range";
//            guiParam = "'min':1, 'max':100";
//            nextViewToBeDisplayed = createAndAddParameterToView(nextViewToBeDisplayed, "difficulty", "range", guiParam, "Difficulty", subtitle);
//
//            // We need to ask for a question count
//            subtitle = "How many questions would you like to generate?";
//            guiParam = "'min':1, 'max':100";
//            nextViewToBeDisplayed = createAndAddParameterToView(nextViewToBeDisplayed, "questionCount", "inputInteger", guiParam, "Question Count", subtitle);
//
//            allViewsAsJson.add("viewKeys", viewKeysJson);
//            allViewsAsJson.add(viewName, nextViewToBeDisplayed);
//        }else{
//            // We are done asking the instructor questions.
//            allViewsAsJson.addProperty("isFinished", true);
//        }
//        System.out.print(allViewsAsJson.toString());
//        return allViewsAsJson.toString();
//    }
//
//    public static JsonObject createAndAddParameterToView(JsonObject viewBeingConstructed, String name, String guiStyle, String guiParameters, String title, String subtitle){
//        // Formatted to illustrate the construction of JSON.
//        String hasParameters = guiParameters.length() > 0 ? "', " : "'";
//        String result = "{'gui': {'style': '" + guiStyle + hasParameters + guiParameters +"},";
//        result += "'title': '"+ title +"',";
//        result += "'subtitle': '"+ subtitle + "'";
//        result += "}";
//        result = result.replace("'", "\"");
//
//        //Add the parameter info to the parameter info object.
//        JsonObject parameter = new JsonParser().parse(result).getAsJsonObject();
//        JsonObject parameterInfo;
//
//        // Check if parameter info is defined.
//        parameterInfo = viewBeingConstructed.has("parameterInfo") ? viewBeingConstructed.getAsJsonObject("parameterInfo") : new JsonObject();
//        parameterInfo.add(name, parameter);
//        viewBeingConstructed.add("parameterInfo", parameterInfo);
//
//        // Add parameter to the view JsonObject to the view.
//        viewBeingConstructed.add(name, parameter);
//
//        // Check if parameterKeys are defined.
//        JsonArray parameterKeys = viewBeingConstructed.has("parameterKeys") ? viewBeingConstructed.getAsJsonArray("parameterKeys") : new JsonArray();
//        parameterKeys.add(name);
//        viewBeingConstructed.add("parameterKeys", parameterKeys);
//        return viewBeingConstructed;
//    }
//
//    // Creates the questions for remind.
//    public static List<String> createQuestions(String allViewsSeenStr) {
//        // Parse the string into a viewSeenAsJson object.
//        JsonObject allViewsSeen = new JsonParser().parse(allViewsSeenStr).getAsJsonObject();
//        List<String> questionList = new ArrayList<String>();
//        JsonArray viewKeysJson = allViewsSeen.getAsJsonArray("viewKeys");
//        //Take the parameters of difficulty and question count
//        JsonObject difficulty = allViewsSeen.getAsJsonObject("start").getAsJsonObject("difficulty");
//        int questionCount = allViewsSeen.getAsJsonObject("start").getAsJsonObject("questionCount").getAsJsonPrimitive("value").getAsInt();
//        double minDifficulty = difficulty.getAsJsonPrimitive("min").getAsDouble();
//        double maxDifficulty = difficulty.getAsJsonPrimitive("max").getAsDouble();
//        JsonArray topics = allViewsSeen.getAsJsonObject("start").getAsJsonObject("topics").getAsJsonArray("values");
//        System.out.println(topics.toString());
//        System.out.println("Those are the topics");
//        System.out.println(allViewsSeen.getAsJsonObject("start").toString());
//        System.out.println("That is view start");
//        List<ProblemTopic> topicsArrList = new ArrayList<ProblemTopic>(topics.size());
//        for(JsonElement topic : topics){
//            String topicName = topic.getAsJsonPrimitive().getAsString();
//            switch(topicName){
//                case "array":
//                    topicsArrList.add(ProblemTopic.ARRAY);
//                    break;
//                case "forLoops":
//                    topicsArrList.add(ProblemTopic.FOR_LOOP);
//                    break;
//                case "conditional":
//                    topicsArrList.add(ProblemTopic.CONDITIONAL);
//                    break;
//                case "array_2d":
//                    topicsArrList.add(ProblemTopic.ARRAY_2D);
//                default:
//                    List<String> errorList = new ArrayList<String>();
//                    String error = "{'error':'There was an error with the topics selected'}";
//                    errorList.add(error.replace("'", "\""));
//                    return errorList;
//            }
//        }
//        ProblemTopic[] topicsArr = new ProblemTopic[topicsArrList.size()];
//        topicsArr = topicsArrList.toArray(topicsArr);
//
//        for(ProblemTopic topic : topicsArr){
//            System.out.print(topic.toString());
//        }
//
//        //We need to create each question
//        CodeGenerator generator = new CodeGenerator(topicsArr);
//        int problemNum = 1;
//        for (Problem problem : generator.generate(minDifficulty, maxDifficulty, questionCount)) {
//            String questionPartPrompt = JavaFormat.INSTANCE.formatFunction(problem.getMainFunction(), ""); // The questionPart prompt
//            Executor exec = new Executor();
//            Output output = exec.execute(problem.getMainFunction());
//            String correct = output.getReturnVar().toString(); // The correct answer
//
//            //problem is the Problem returned by the CodeGenerator
//            //DistractorGenerator dGen = new DistractorGenerator(problem);
//            //List<String> distractors = dGen.getDistractors(numOfDistractors);
//            /*
//            for(String distractor : distractors){
//                System.out.print("Troys distractor is: " + distractor);
//            }
//            */
//            int correctVal = Integer.parseInt(correct);
//
//            // Lets get some values that are generally in the range of the correct answer.
//            Random rand = new Random();
//            int minC;
//            int maxC;
//            //account for negative numbers to prevent a negative bound being sent to random.nextInt
//            if(correctVal < 0){
//                minC = Math.abs(correctVal) - 20;
//                maxC = Math.abs(correctVal) + 20;
//            }
//            else{
//                minC = correctVal - 20;
//                maxC = correctVal + 20;
//            }
//            System.out.println("correctVal: "+correctVal+" minC: "+minC + " maxC: "+maxC);
//
//            int incorrect1 = rand.nextInt(maxC)+minC;
//            int incorrect2 = rand.nextInt(maxC-5)+ minC;
//            int incorrect3 = rand.nextInt(maxC+5)+minC-5;
//            if(incorrect1 == correctVal){
//                incorrect1++;
//            }
//            if(incorrect2 == correctVal){
//                incorrect2++;
//            }
//            if(incorrect3 == correctVal){
//                incorrect3++;
//            }
//
//            //for negative correctVal's, flip signs back for incorrect values
//            if(correctVal < 0){
//                incorrect1 = -1 * incorrect1;
//                incorrect2 = -1 * incorrect2;
//                incorrect3 = -1 * incorrect3;
//            }
//
//            JsonArray choices = new JsonArray();
//            JsonObject correctChoice = createChoice("text", true, Integer.toString(correctVal));
//            choices.add(correctChoice);
//
//
//            JsonObject incorrectChoice = createChoice("text", false, Integer.toString(incorrect1));
//            choices.add(incorrectChoice);
//            incorrectChoice = createChoice("text", false, Integer.toString(incorrect2));
//            choices.add(incorrectChoice);
//            incorrectChoice = createChoice("text", false, Integer.toString(incorrect3));
//            choices.add(incorrectChoice);
//
//            //Make question part
//            JsonObject questionPart = createQuestionPart(questionPartPrompt, 0, choices);
//            JsonArray questionParts = new JsonArray();
//            questionParts.add(questionPart);
//
//            JsonObject question = new JsonObject();
//            question.addProperty("format", "Multiple Choice");
//            question.addProperty("prompt", "What is the correct answer?");
//            question.add("parts", questionParts);
//            questionList.add(question.toString());
//            problemNum++;
//        }
//        return questionList;
//    }
//
//
//    public static JsonObject createChoice(String style, Boolean isCorrect, String value){
//        JsonObject choice = new JsonObject();
//        choice.addProperty("style", style);
//        choice.addProperty("isCorrect", isCorrect);
//        choice.addProperty("value", value);
//        return choice;
//    }
//    public static JsonObject createQuestionPart(String prompt, int orderIndex, JsonArray choices){
//        JsonObject questionPart = new JsonObject();
//        questionPart.addProperty("prompt", prompt);
//        questionPart.addProperty("orderIndex", orderIndex);
//        questionPart.add("choices",choices);
//        return questionPart;
//    }
//}

package com.troystopera.gencode;

import com.troystopera.gencode.generator.CodeGenerator;
import com.troystopera.gencode.generator.distractors.DistractorGenerator;
import com.troystopera.jkode.exec.Executor;
import com.troystopera.jkode.exec.Output;
import com.troystopera.jkode.format.JavaFormat;
import com.troystopera.jkode.vars.JVar;

import java.lang.*;
import java.util.*;

/**
 * Created by troy on 8/1/17.
 */
public class Main {

    public static void main(String... args) {
        if (args.length < 4) {
            System.out.println("Please provide difficulty range, number of questions, and at least one topic");
            System.exit(-1);
        }

        double difficultyLow = Double.valueOf(args[0]);
        double difficultyHigh = Double.valueOf(args[1]);
        int count = Integer.valueOf(args[2]);
        ProblemTopic[] topics = new ProblemTopic[args.length - 3];

        for (int i = 3; i < args.length; i++)
            topics[i - 3] = ProblemTopic.valueOf(args[i]);

        System.out.println("Topics: " + Arrays.toString(topics));
        System.out.println("Difficulty: " + difficultyLow + " -> " + difficultyHigh);
        System.out.println("-------\n");

        CodeGenerator generator = new CodeGenerator(topics);

        int problemNum = 1;
        for (Problem problem : generator.generate(difficultyLow, difficultyHigh, count)) {
            System.out.println("Problem: " + problemNum);
            System.out.println(JavaFormat.INSTANCE.formatFunction(problem.getMainFunction(), ""));

            Executor exec = new Executor();
            Output output = exec.execute(problem.getMainFunction());
            JVar answer = output.getReturnVar();
            // temporary fix for 2d array return values
            while (answer == null) {
                problem = generator.generate(problem.getDifficulty());
                output = exec.execute(problem.getMainFunction());
                answer = output.getReturnVar();
            }

            System.out.println("Answer: " + answer.toString());

            // DistractorGenerator dist = new DistractorGenerator(problem);
            // List<String> distractors = dist.getDistractors(3);
            // generate a random number from 0-3 to choose which spot the correct answer goes in list
            // int rand = (int) (Math.random() * 4);
            // count = 0;
            // String returnVal = output.getReturnVar().toString();
            // System.out.print("Answer choices: ");
            /* for (String s: distractors) {
                if (count == rand) {
                    System.out.print(returnVal + " ");
                }
                System.out.print(s + " ");
                count++;
            }
            if (rand == 3) {
                System.out.print(returnVal);
            } */

            // System.out.println("\nCorrect answer: " + returnVal);
            System.out.println("\n\n");
            problemNum++;
        }
    }
}

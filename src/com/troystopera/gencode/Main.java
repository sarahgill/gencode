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
            Executor exec = new Executor();
            Output output = exec.execute(problem.getMainFunction());
            JVar answer = output.getReturnVar();
            System.out.println(JavaFormat.INSTANCE.formatFunction(problem.getMainFunction(), ""));
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
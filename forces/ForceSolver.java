package com.saptakdas.physics.forces;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Scanner;

public class ForceSolver {
    private static Scanner sc = new Scanner(System.in);

    public enum Direction{UP, DOWN, EAST, WEST, ZERO}
    public enum ForceType{air, gravity, applied, friction, normal, tension}
    private static ScriptEngineManager manager = new ScriptEngineManager();
    private static ScriptEngine engine = manager.getEngineByName("js");

    static class Acceleration{
        public Double magnitude;
        public Direction direction;

        public Acceleration(Double magnitude, Direction direction) {
            this.magnitude = magnitude;
            this.direction=direction;
        }

        Acceleration() {}
    }

    static class Force extends Acceleration{
        public String name="";
        public ForceType type;
        public Force() {
            ForceType type=null;
            while (true) {
                System.out.println("\nEnter Force Attributes:");
                System.out.print("Type(air, gravity, applied, friction, normal, tension): ");
                String textType = sc.next();
                boolean matched=false;
                for (ForceType t: ForceType.values()){
                    if(t.toString().toLowerCase().equals(textType.toLowerCase())){
                        type=t;
                        matched=true;
                        break;
                    }
                }
                if(!matched) {
                    System.out.println("Please enter an applicable force!");
                }else {
                    break;
                }
            }
            this.type=type;
            boolean askMoreInputs=true;
            if(type==ForceType.gravity || type==ForceType.normal){
                while(true) {
                    System.out.println("If no measurement is provided and force is equal, normal force will be set to the opposite of weight.");
                    System.out.print("Is force measurement given(y/n)? ");
                    String answer=sc.next();
                    if(answer.contains("y") || answer.contains("n")) {
                        askMoreInputs = answer.contains("y");
                        break;
                    }else{
                        System.out.println("Please indicate yes or no.");
                    }
                }
            }
            if(askMoreInputs) {
                this.magnitude = input("Magnitude");
                Direction direction = Direction.ZERO;
                while (true) {
                    System.out.print("Direction(UP, DOWN, EAST, WEST): ");
                    String textDirection = sc.next();
                    boolean matched = false;
                    for (Direction dir : Direction.values()) {
                        if (dir == Direction.ZERO)
                            continue;
                        if (dir.toString().equals(textDirection.toUpperCase())) {
                            direction = dir;
                            matched=true;
                            break;
                        }
                    }
                    if (!matched) {
                        System.out.println("Please enter a proper direction!");
                    } else {
                        break;
                    }
                }
                this.direction = direction;
            }else{
                this.magnitude=0.0;
                this.direction=Direction.ZERO;
            }
            System.out.println();
        }

        public Force(ForceType type){
            this.type=type;
            this.magnitude=null;
            this.direction=null;
        }

        @Override
        public String toString() {
            return "Force("+this.type+(!this.name.equals("") ?"["+this.name+"]": "")+")";
        }
    }

    public static void main(String[] args) {
        //Get input
        System.out.println("This is a Forces solver. Restrictions of this include only working in two dimensions and working with independent equations. Please enter all field values using same units throughout.");
        System.out.println("Enter all given fields.\n");
        //Mass Input
        boolean massGiven;
        while (true) {
            System.out.print("Is mass given(y/n)? ");
            String answer = sc.next();
            if (answer.contains("y") || answer.contains("n")) {
                massGiven = answer.contains("y");
                break;
            } else {
                System.out.println("Please indicate yes or no.");
            }
        }
        Double mass = null;
        if (massGiven) {
            mass = input("Mass");
        }
        if (mass == 0) {
            System.out.println("Mass of 0 not allowed.");
            System.exit(0);
        }
        //AccelerationX Input
        boolean accelerationXGiven;
        while (true) {
            System.out.print("Is acceleration in horizontal direction given(y/n)? ");
            String answer = sc.next();
            if (answer.contains("y") || answer.contains("n")) {
                accelerationXGiven = answer.contains("y");
                break;
            } else {
                System.out.println("Please indicate yes or no.");
            }
        }
        Acceleration accelerationX = null;
        if (accelerationXGiven) {
            Double tempInput = input("Acceleration in horizontal direction");
            accelerationX = new Acceleration(tempInput, (tempInput >= 0 ? Direction.EAST : Direction.WEST));
        }

        //AccelerationY Input
        boolean accelerationYGiven;
        while (true) {
            System.out.print("Is acceleration in vertical direction given(y/n)? ");
            String answer = sc.next();
            if (answer.contains("y") || answer.contains("n")) {
                accelerationYGiven = answer.contains("y");
                break;
            } else {
                System.out.println("Please indicate yes or no.");
            }
        }
        Acceleration accelerationY = null;
        if (accelerationYGiven) {
            Double tempInput = input("Acceleration in vertical direction");
            accelerationY = new Acceleration(tempInput, (tempInput >= 0 ? Direction.UP : Direction.DOWN));
        }

        //Get List of Forces
        Boolean forcesGiven = null;
        while (true) {
            System.out.print("\nAre any forces given(y/n)? ");
            String answer = sc.next();
            if (answer.contains("y") || answer.contains("n")) {
                forcesGiven = answer.contains("y");
                break;
            } else {
                System.out.println("Please indicate yes or no.");
            }
        }
        LinkedList<Force> givenForces = new LinkedList<>();
        if (forcesGiven) {
            givenForces = allForces();
        }
        //Adjusting unfilled forces if possible
        Double gravity = null;
        for (Force f : givenForces) {
            if (f.direction == Direction.ZERO && massGiven) {
                if (gravity == null) {
                    gravity = input("Gravity(Magnitude only)");
                }
                f.direction = (f.type == ForceType.gravity ? Direction.DOWN : Direction.UP);
                f.magnitude = mass * gravity;
            }
        }
        //Get unknown
        Hashtable<String, Object> unknown = new Hashtable<>();
        int fieldsFilled = 0;
        System.out.println("\nEnter the unknown fields(up to 2).");
        while (fieldsFilled < 2) {
            System.out.println();
            String fieldType = null;
            while (true) {
                System.out.print("Field type(Force, Acceleration, Mass): ");
                String unknownField = sc.next();
                for (String fieldName : new String[]{"f", "a", "m"}) {
                    if (unknownField.toLowerCase().startsWith(fieldName)) {
                        fieldType = fieldName;
                    }
                }
                if (fieldType == null) {
                    System.out.println("Please enter a valid field type.");
                } else {
                    break;
                }
            }
            if (fieldType.equals("f")) {
                ForceType type = null;
                while (true) {
                    System.out.print("Force Type(air, gravity, applied, friction, normal, tension): ");
                    String textType = sc.next();
                    boolean matched = false;
                    for (ForceType t : ForceType.values()) {
                        if (t.toString().toLowerCase().equals(textType.toLowerCase())) {
                            type = t;
                            matched = true;
                            break;
                        }
                    }
                    if (!matched) {
                        System.out.println("Please enter an applicable force!");
                    } else {
                        break;
                    }
                }
                boolean XOrY;
                while (true) {
                    System.out.print("Force axis(x/y)? ");
                    String answer = sc.next().toLowerCase();
                    if (answer.contains("x") || answer.contains("y")) {
                        XOrY = answer.contains("x");
                        break;
                    } else {
                        System.out.println("Please indicate yes or no.");
                    }
                }
                unknown.put("Force" + (XOrY ? "X" : "Y"), new Force(type));
            } else if (fieldType.equals("a")) {
                boolean XOrY;
                while (true) {
                    System.out.print("Acceleration axis(x/y)? ");
                    String answer = sc.next().toLowerCase();
                    if (answer.contains("x") || answer.contains("y")) {
                        XOrY = answer.contains("x");
                        break;
                    } else {
                        System.out.println("Please indicate yes or no.");
                    }
                }
                if ((XOrY && !accelerationXGiven) || (!XOrY && !accelerationYGiven)) {
                    unknown.put("Acceleration" + (XOrY ? "X" : "Y"), new Acceleration(null, null));
                    if (XOrY) {
                        accelerationXGiven = true;
                    } else {
                        accelerationYGiven = true;
                    }
                } else {
                    System.out.println("This field has already been used. Please try again.");
                    System.exit(0);
                }
            } else {
                unknown.put("Mass", "Mass");
            }
            fieldsFilled += 1;
            if (fieldsFilled == 1) {
                boolean addForces;
                while (true) {
                    System.out.print("Add more unknowns(y/n)? ");
                    String answer = sc.next();
                    if (answer.contains("y") || answer.contains("n")) {
                        addForces = answer.contains("y");
                        break;
                    } else {
                        System.out.println("Please indicate yes or no.");
                    }
                }
                if (!addForces) {
                    break;
                }
            }
        }


        //Solve
        //Splitting Forces into x or y
        LinkedList<Force> forcesX = new LinkedList<>();
        LinkedList<Force> forcesY = new LinkedList<>();
        for (Force f : givenForces) {
            if (f.direction == Direction.EAST || f.direction == Direction.WEST) {
                forcesX.addLast(f);
            } else {
                forcesY.addLast(f);
            }
        }
        Hashtable<String, Integer> forceCount = new Hashtable<>();
        for (Force f : forcesX) {
            if (forceCount.containsKey(f.type.toString())) {
                forceCount.put(f.type.toString(), forceCount.get(f.type.toString()) + 1);
            } else {
                forceCount.put(f.type.toString(), 1);
            }
        }
        Hashtable<String, Integer> newForceCount = new Hashtable<>();
        for (Force f : forcesX) {
            if (forceCount.get(f.type.toString()) > 1) {
                if (newForceCount.containsKey(f.type.toString())) {
                    f.name = newForceCount.get(f.type.toString()).toString();
                    newForceCount.put(f.type.toString(), newForceCount.get(f.type.toString()) + 1);
                } else {
                    f.name = "1";
                    newForceCount.put(f.type.toString(), 2);
                }
            }
        }
        forceCount = new Hashtable<>();
        for (Force f : forcesY) {
            if (forceCount.containsKey(f.type.toString())) {
                forceCount.put(f.type.toString(), forceCount.get(f.type.toString()) + 1);
            } else {
                forceCount.put(f.type.toString(), 1);
            }
        }
        newForceCount = new Hashtable<>();
        for (Force f : forcesY) {
            if (forceCount.get(f.type.toString()) > 1) {
                if (newForceCount.containsKey(f.type.toString())) {
                    f.name = newForceCount.get(f.type.toString()).toString();
                    newForceCount.put(f.type.toString(), newForceCount.get(f.type.toString()) + 1);
                } else {
                    f.name = "1";
                    newForceCount.put(f.type.toString(), 2);
                }
            }
        }
        //Iterating to find num of unknowns
        int unknownX = 0;
        int unknownY = 0;
        for (String key : unknown.keySet()) {
            if (key.equals("Mass")) {
                unknownX += 1;
                unknownY += 1;
            } else if (key.equals("AccelerationX") || key.equals("ForceX")) {
                unknownX += 1;
            } else {
                unknownY += 1;
            }
        }
        if (unknown.containsKey("Mass") && accelerationXGiven) {
            if (accelerationX.magnitude == 0) {
                unknownX -= 1;
            }
        } else if (unknown.containsKey("Mass") && accelerationYGiven) {
            if (accelerationY.magnitude == 0) {
                unknownY -= 1;
            }
        }

        //GUESS
        //Givens
        System.out.println("\nG:");
        if (massGiven) {
            System.out.println("-->Mass=" + mass);
        }
        if (accelerationX != null) {
            System.out.println("-->AccelerationX=" + accelerationX.magnitude + ", " + accelerationX.direction);
        }
        if (accelerationY != null) {
            System.out.println("-->AccelerationY=" + accelerationY.magnitude + ", " + accelerationY.direction);
        }
        for (Force f : givenForces) {
            System.out.println("-->" + f.toString() + "=" + f.magnitude + ", " + f.direction.toString());
        }

        //U
        System.out.println("\nU:");
        for (String key : unknown.keySet()) {
            System.out.println("-->" + key + "=?");
        }

        //Equation Development
        System.out.println("\nE:");
        Integer[] unknownsArr = {unknownX, unknownY};
        int iterationNum = 1;
        String unknownFieldX = "";
        String unknownFieldY = "";
        Force unknownForceX = null;
        Force unknownForceY = null;
        for (Integer u : unknownsArr) {
            if (u == 0) {
                //No unknown values to be solved. End at "E" step
                if (iterationNum == 1) {
                    StringBuilder sumOfForces = new StringBuilder();
                    for (Force f : forcesX) {
                        sumOfForces.append(f.toString()).append("+");
                    }
                    if (sumOfForces.length() != 0) {
                        sumOfForces = sumOfForces.deleteCharAt(sumOfForces.length() - 1);
                        System.out.println("Sum of Forces in X=" + sumOfForces + "=0");
                    } else {
                        System.out.println("Sum of Forces in X=0");
                    }
                } else {
                    StringBuilder sumOfForces = new StringBuilder();
                    for (Force f : forcesY) {
                        sumOfForces.append(f.toString()).append("+");
                    }
                    if (sumOfForces.length() != 0) {
                        sumOfForces = sumOfForces.deleteCharAt(sumOfForces.length() - 1);
                        System.out.println("Sum of Forces in Y=" + sumOfForces + "=0");
                    } else {
                        System.out.println("Sum of Forces in Y=0");
                    }
                }
            } else if (u == 1) {
                //Check if any forces are unknown
                if (iterationNum == 1) {
                    for (Force f : forcesX) {
                        if (f.direction == Direction.ZERO) {
                            unknownsArr[0] += 1;
                        }
                    }
                    if (unknownsArr[0] > 1) {
                        System.out.println("This equation has too many unknowns and likely isn't solvable.");
                        System.exit(0);
                    }
                    //Solve now.
                    StringBuilder sumOfForces = new StringBuilder();
                    for (Force f : forcesX) {
                        sumOfForces.append(f.toString()).append("+");
                    }
                    sumOfForces = sumOfForces.deleteCharAt(sumOfForces.length() - 1);
                    for (String key : unknown.keySet()) {
                        if (key.equals("Mass") && accelerationXGiven) {
                            if (accelerationX.magnitude != 0) {
                                unknownFieldX = "m";
                                break;
                            }
                        } else if (key.equals("AccelerationX")) {
                            unknownFieldX = "a";
                            break;
                        } else {
                            unknownFieldX = "f";
                            unknownForceX = (Force) unknown.get(key);
                            break;
                        }
                    }
                    System.out.println("Sum of Forces in X=" + (unknownForceX!=null? unknownForceX.toString()+"+": "") + sumOfForces + "=mass*AccelerationX");
                    if (unknownFieldX.equals("m")) {
                        System.out.println("mass=(" + sumOfForces + ")/AccelerationX");
                    } else if (unknownFieldX.equals("a")) {
                        System.out.println("AccelerationX=(" + sumOfForces + ")/mass");
                    } else {
                        System.out.println(unknownForceX.toString() + "=" + "(mass*AccelerationX)"+"-("+sumOfForces+")");
                    }
                    System.out.println();
                } else {
                    for (Force f : forcesY) {
                        if (f.direction == Direction.ZERO) {
                            unknownsArr[1] += 1;
                        }
                    }
                    if (unknownsArr[1] > 1) {
                        System.out.println("This equation has too many unknowns and likely isn't solvable.");
                        System.exit(0);
                    }
                    //Solve now.
                    StringBuilder sumOfForces = new StringBuilder();
                    for (Force f : forcesY) {
                        sumOfForces.append(f.toString()).append("+");
                    }
                    sumOfForces = sumOfForces.deleteCharAt(sumOfForces.length() - 1);
                    for (String key : unknown.keySet()) {
                        if (key.equals("Mass") && accelerationYGiven) {
                            if (accelerationY.magnitude != 0) {
                                unknownFieldY = "m";
                                break;
                            }
                        } else if (key.equals("AccelerationY")) {
                            unknownFieldY = "a";
                            break;
                        } else {
                            unknownFieldY = "f";
                            unknownForceY = (Force) unknown.get(key);
                            break;
                        }
                    }
                    System.out.println("Sum of Forces in Y=" + (unknownForceY!=null? unknownForceY.toString()+"+": "")+sumOfForces + "=mass*AccelerationY");
                    if (unknownFieldY.equals("m")) {
                        System.out.println("mass=(" + sumOfForces + ")/AccelerationY");
                    } else if (unknownFieldY.equals("a")) {
                        System.out.println("AccelerationY=(" + sumOfForces + ")/mass");
                    } else {
                        System.out.println(unknownForceY.toString() + "=" + "(mass*AccelerationY)"+"-("+sumOfForces+")");
                    }
                }
            } else if (u > 1) {
                //Too many vars
                System.out.println("This equation has too many unknowns and likely isn't solvable.");
                System.exit(0);
            }
            iterationNum += 1;
        }
        //S
        System.out.println("\nS:");
        iterationNum = 1;
        String equationX = null;
        String equationY = null;
        for (Integer ignored : unknownsArr) {
            if (iterationNum == 1) {
                StringBuilder sumOfForces = new StringBuilder();
                for (Force f : forcesX) {
                    sumOfForces.append(f.toString()).append("+");
                }
                if (sumOfForces.length() != 0) {
                    sumOfForces = sumOfForces.deleteCharAt(sumOfForces.length() - 1);
                }
                for (Force f : forcesX) {
                    sumOfForces = new StringBuilder(sumOfForces.toString().replace(f.toString(), "(" + (f.direction == Direction.WEST ? "-" : "") + f.magnitude + ")"));
                }
                String equation = null;
                if (unknownFieldX.equals("m")) {
                    equation = "mass=(" + sumOfForces + ")/AccelerationX";
                    equation = equation.replace("AccelerationX", "(" + (accelerationX.direction == Direction.WEST ? "-" : "") + accelerationX.magnitude + ")");
                    System.out.println(equation);
                } else if (unknownFieldX.equals("a")) {
                    equation = "AccelerationX=(" + sumOfForces + ")/mass";
                    equation = equation.replace("mass", "(" + mass.toString() + ")");
                    System.out.println(equation);
                } else if (unknownFieldX.equals("f")) {
                    equation = unknownForceX.toString() + "=" + "(mass*AccelerationX)"+"-("+sumOfForces+")";
                    equation = equation.replace("mass", mass.toString());
                    equation = equation.replace("AccelerationX", "(" + (accelerationX.direction == Direction.WEST ? "-" : "") + accelerationX.magnitude + ")");
                    System.out.println(equation);
                }
                equationX = equation;
                System.out.println();
            } else {
                StringBuilder sumOfForces = new StringBuilder();
                for (Force f : forcesY) {
                    sumOfForces.append(f.toString()).append("+");
                }
                if (sumOfForces.length() != 0) {
                    sumOfForces = sumOfForces.deleteCharAt(sumOfForces.length() - 1);
                }
                for (Force f : forcesY) {
                    sumOfForces = new StringBuilder(sumOfForces.toString().replace(f.toString(), "(" + (f.direction == Direction.DOWN ? "-" : "") + f.magnitude + ")"));
                }
                String equation = null;
                if (unknownFieldY.equals("m")) {
                    equation = "mass=(" + sumOfForces + ")/AccelerationY";
                    equation = equation.replace("AccelerationY", "(" + (accelerationY.direction == Direction.DOWN ? "-" : "") + accelerationY.magnitude + ")");
                    System.out.println(equation);
                } else if (unknownFieldY.equals("a")) {
                    equation = "AccelerationY=(" + sumOfForces + ")/mass";
                    equation = equation.replace("mass", "(" + mass.toString() + ")");
                    System.out.println(equation);
                } else if (unknownFieldY.equals("f")) {
                    equation = unknownForceY.toString() + "=" + "(mass*AccelerationY)"+"-("+sumOfForces+")";
                    equation = equation.replace("mass", mass.toString());
                    equation = equation.replace("AccelerationY", "(" + (accelerationY.direction == Direction.DOWN ? "-" : "") + accelerationY.magnitude + ")");
                    System.out.println(equation);
                }
                equationY = equation;
            }
            iterationNum += 1;
        }
        //S
        System.out.println("\nS:");
        iterationNum = 1;
        for (Integer ignored : unknownsArr) {
            if (iterationNum == 1) {
                if (!unknownFieldX.equals("")) {
                    try {
                        String answer = engine.eval(equationX.split("=")[1]).toString();
                        System.out.println(equationX.split("=")[0] + "=" + answer+(equationX.split("=")[0].contains("mass")? " kg": (equationX.split("=")[0].contains("Force")? " N": " m/s^2"))+"="+Math.abs(Double.parseDouble(answer))+(equationX.split("=")[0].contains("mass")? " kg": (equationX.split("=")[0].contains("Force")? " N": " m/s^2"))+", "+(answer.startsWith("-")?"WEST": "EAST"));
                    } catch (ScriptException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println();
            } else {
                if (!unknownFieldY.equals("")) {
                    try {
                        String answer = engine.eval(equationY.split("=")[1]).toString();
                        System.out.println(equationY.split("=")[0] + "=" + answer+(equationY.split("=")[0].contains("mass")? " kg": (equationY.split("=")[0].contains("Force")? " N": " m/s^2"))+"="+Math.abs(Double.parseDouble(answer))+(equationY.split("=")[0].contains("mass")? " kg": (equationY.split("=")[0].contains("Force")? " N": " m/s^2"))+", "+(answer.startsWith("-")?"DOWN": "UP"));
                    } catch (ScriptException e) {
                        e.printStackTrace();
                    }
                }
            }
            iterationNum += 1;
        }
    }

    public static double input(String variable) {
        String value;
        while (true) {
            System.out.print(variable + ": ");
            value = sc.next();
            try {
                Double.parseDouble(value);
                break;
            } catch (Exception e) {
                System.out.println("Please enter a number!");
            }
        }
        return Double.parseDouble(value);
    }

    public static LinkedList<Force> allForces() {
        LinkedList<Force> inputs = new LinkedList<>();
        while (true) {
            inputs.addLast(new Force());
            boolean addForces;
            while(true) {
                System.out.print("Add more forces(y/n)? ");
                String answer=sc.next();
                if(answer.contains("y") || answer.contains("n")) {
                    addForces = answer.contains("y");
                    break;
                }else{
                    System.out.println("Please indicate yes or no.");
                }
            }
            if(!addForces){
                break;
            }
        }
        return inputs;
    }
}
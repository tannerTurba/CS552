/*
 * Tanner Turba
 * November 1, 2023
 * CS 552 - Artificial Intelligence - Assignment 2
 * 
 * This class contains global information that defines the 
 * program's behavior. 
 */
public class Config {
    public static VarOrdering orderingHeuristic = VarOrdering.STATIC;
    public static IterationType valueOrder = IterationType.STATIC;
    public static boolean isLimitedForwardChecking = false;
    public static boolean shouldPreprocess = false;
    public static boolean isVerbosity2 = false;
}

package ChocoKt

import org.chocosolver.solver.Model
import org.chocosolver.solver.variables.Task

/**
 *
 * Scheduling problem
 * Cumulative constraint
 *
 * Prolog program:
 *
 * scheduling_problem(Tasks, [S1,S2,S3,D1,D2,D3]) :-
 *    Sum is D1 + D2 + D3,
 *    [S1,S2,S3] ins 1..Sum,
 *    Tasks = [task(S1,D1,_,3,_),task(S2,D2,_,2,_),task(S3,D3,_,2,_)].
 *
 * query:
 *
 * Vars = [S1,S2,S3,60,45,30], scheduling_problem(Tasks,Vars), cumulative(Tasks, [limit(4)]), label(Vars).
 *
 * Result:
 *
 * S1 = 1,
 * S2 = S3, S3 = 61,
 * Tasks = [
 *    task(1,60,61,3,_1884),
 *    task(61,45,106,2,_1902),
 *    task(61,30,91,2,_592)
 * ],
 * Vars = [1, 61, 61, 60, 45, 30]
 *
 */

fun main() {

    val model = Model()

    // definition of the variables

    val numTasks = 3
    val durations = intArrayOf(60,45,30)
    val capacities = intArrayOf(3,2,2)
    val maxCapacity = 4
    val sum = durations.sum()
    val starts = model.intVarArray("starts", numTasks, 0, sum)

    // definition of constraints

    model.cumulative(starts, durations, capacities, maxCapacity)

    // search for a solution
    



}
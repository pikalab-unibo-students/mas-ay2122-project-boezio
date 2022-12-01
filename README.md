# School Timetable :school:

This repository contains a project realized as case study for the *Multi-Agent Systems* exam of [Master's degree in Artificial Intelligence, University of Bologna](https://corsi.unibo.it/2cycle/artificial-intelligence).

In this project it is described a problem which involves both Constraint Programming and Multi-Agent
Systems. The problem consists of finding a school timetable for each professor of a school such that
different constraints related to different aspects of the problem are respected, and trying to satisfy
all preferences of each professor as much as possible.

## Description

Two types of agents are used to simulate a real scenario: **Time-Scheduler** and **Professor**

**Time-Scheduler** is responsible for:
* creation of timetables
* communication of timetables to each professor
* mediation of negotiation among professors for the satisfaction of their preferences

**Professor** agent performs the following tasks:
* receive the timetable
* try to satisfy own preferences
* decide whether to accept a lesson change or not

There is a single Time-Scheduler agent and more Professor agents depending on the specific instance of the problem.

Each professor has one or more **preferences** which correspond to the lessons (hours and day) the professor would prefer not to be available for teaching.

## Communication

When a professor wants to change an own lesson in order to satisfy a preference, a call-for-proposal (cfp) message is sent to the time-scheduler to begin the negotiation. The time-scheduler will retrieve the list of all professors who teach in the same class of the proposer’s lesson. To have a reasonable change it is also important to consider that for each candidate professor and for each candidate lesson:
* the proposer must be free during a candidate lesson
* the candidate professor must be free during the lesson the time-scheduler wants to propose
* the proposer’s lesson cannot be in the free day of the candidate professor and the candidate lesson cannot be in the free day of the proposer professor

Following these criteria, the time-scheduler will realize a list of pair professor-lesson and will try to satisfy the proposer’s request with one of them.
The time-scheduler sends to the candidate professor a proposal and this proposal can be accepted according to two criteria:
* the proposed lesson is not one of own preferences
* the lesson to give must not be in the locked preferences
If these criteria are met, the candidate professor replies with an agree message otherwise refuses.
If the reply message contains agree as performative, the time-scheduler communicates to the proposer the possible change. The proposer will accept or reject according to the fact that the lesson change does not worsen own preferences.
According to the last message received from the proposer, the time-scheduler will send to the candidate, who previously accepted the change, a confirm or disconfirm message.
If all possible negotiations fail, the time-scheduler will send to the proposer a refuse message.

## Framework and Libraries

The project has been developed mainly in Java using the [*Jade*](https://jade.tilab.com/) MAS framework.
The Constraint Programming model defined to find a feasible timetable for all Professors has been realized with Kotlin language using the following libraries:
* [*2p-Kt*](https://apice.unibo.it/xwiki/bin/view/Tuprolog/): Prolog interpreter written in Kotlin
* [*Choco Solver*](https://choco-solver.org/): used to create a library of CP


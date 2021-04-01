# PhysicsSolvers
These are solvers to express classic motion problems in simplfied ways. The topics include Kinematics and Forces.

## Kinematics Solver
This is a simple kinematics solver that uses GUESS method to solve problems given any three fields. Note this program assumes constant acceleration(inertial frame).

## Forces Solver
This is a simple forces solver that uses GUESS method to solve problems given all but one field. This solver treats both x and y dimensions as independent components. Force Types included are air, gravity, applied, friction, normal, and tension. The solver uses F=ma to solve all unknowns. 

# Note
These solvers use Nashorn Engine which has been removed from JDK versions after 15.0. In order to solve, a JDK of 11.x or 12.x is prefered.

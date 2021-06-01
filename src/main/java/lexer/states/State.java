package lexer.states;

public class State {
    StateType state;
    public void setState(StateType state){
        this.state = state;
    }
    public StateType getState(){
        return state;
    }
}

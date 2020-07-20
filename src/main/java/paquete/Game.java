package paquete;


import org.springframework.web.socket.WebSocketSession;

public class Game {
	private String gName; //nombre de la partida
	private WebSocketSession p0Session; //id del jugador 0
	private WebSocketSession p1Session; //id del juegador 1
	private String p0Name; // nombre del juegador 0
	private String p1Name; // nombre del jugador 1
	private boolean turn; //false para jugador 0, true para jugador 1
	private int board[]= {2,2,2,2,2,2,2,2,2}; //0 ficha p0, 1 ficha p1, 2 nada
	
	public Game(String gName, WebSocketSession p0Session, String p0Name) {
		this.gName = gName;
		this.p0Session = p0Session;
		this.p0Name = p0Name;
		this.turn = false;
	}
	
	public String getgName() {
		return gName;
	}

	public WebSocketSession getP0Session() {
		return p0Session;
	}

	public WebSocketSession getP1Session() {
		return p1Session;
	}

	public String getP0Name() {
		return p0Name;
	}

	public String getP1Name() {
		return p1Name;
	}

	public boolean isPlayerTurn(WebSocketSession ps) {
		if((ps==p1Session && turn)||(ps==p0Session && !turn)){
			return true;
		}
		return false;
	}
	
	public boolean join(WebSocketSession p1s, String name){
		if(p1Session==null){
			this.p1Session = p1s;
			this.p1Name = name;
			return true;
		}
		return false;
	}
	
	public boolean update(WebSocketSession ps, int pos){
		int ficha;
		if(ps==p0Session){
			ficha = 0;
		}else{
			ficha = 1;
		}
		if(board[pos]==2){
			board[pos]=ficha;
			turn=!turn;
			return true;
		}
		return false;
	}
	
	//0 gana p0, 1 gana p1, 2 tablas, 3 en juego
	public int getState(int ficha){
		if(board[0]==ficha && board[1]==ficha && board[2]==ficha)
		{
			return ficha;
		}
		if(board[3]==ficha && board[4]==ficha && board[5]==ficha)
		{
			return ficha;
		}
		if(board[6]==ficha && board[7]==ficha && board[8]==ficha)
		{
			return ficha;
		}
		if(board[0]==ficha && board[3]==ficha && board[6]==ficha)
		{
			return ficha;
		}
		if(board[1]==ficha && board[4]==ficha && board[7]==ficha)
		{
			return ficha;
			}
		if(board[2]==ficha && board[5]==ficha && board[8]==ficha)
		{
			return ficha;
		}
		if(board[0]==ficha && board[4]==ficha && board[8]==ficha)
		{
			return ficha;
		}
		if(board[2]==ficha && board[4]==ficha && board[6]==ficha)
		{
			return ficha;
		}
		
		for(int i=0; i<9;i++){
			if(board[i]==2) //si queda alguna casilla vacia
				return 3; //continua el juego
		}
		return 2; //tablas
	}

		
}

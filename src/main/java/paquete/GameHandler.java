package paquete;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class GameHandler extends TextWebSocketHandler {

	private Map<String, Game> games = new ConcurrentHashMap<>();
	private static Map<String, Score> scores = new ConcurrentHashMap<>();
	
	private ObjectMapper mapper = new ObjectMapper();
	
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		System.out.println("New user: " + session.getId());
	}
	
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		System.out.println("Session closed: " + session.getId());
	}
	
	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		
		System.out.println("Message received from user "+session.getId()+": " + message.getPayload());
		JsonNode node = mapper.readTree(message.getPayload());
		String action = node.get("action").asText();
		
		if(action.equals("create")){
			createGame(session, node);
		}else if(action.equals("join")){
			joinGame(session, node);
		}else if(action.equals("update")){
			updateGame(session, node);
		}
	}

	private void createGame(WebSocketSession session, JsonNode node) throws IOException{
		String gamename = node.get("gamename").asText();
		ObjectNode respuesta = mapper.createObjectNode();
		
		if(!games.containsKey(gamename)){
			Game newGame = new Game(gamename, session,node.get("username").asText());
			games.put(newGame.getgName(), newGame);
			System.out.println("User "+session.getId()+" create game: " + gamename);
				
			respuesta.put("gamename", newGame.getgName());
			respuesta.put("info", "Has creado: "+newGame.getgName());
			respuesta.put("turn", "none");
		}else{
			respuesta.put("info", "Error: Ya existe una partida con ese nombre.");
		}
		session.sendMessage(new TextMessage(respuesta.toString()));		
		System.out.println("Message sent to user "+session.getId()+": " + respuesta.toString());
	}
	
	private void joinGame(WebSocketSession session, JsonNode node) throws IOException{
		String gamename = node.get("gamename").asText();
		String username = node.get("username").asText();
		ObjectNode respuestaP1 = mapper.createObjectNode();
		Game game;
		ObjectNode respuestaP0; 
		
		if(games.containsKey(gamename)){
			game = games.get(gamename);
			respuestaP0 = mapper.createObjectNode();
			
			if(game.join(session, username)){
					System.out.println("User "+session.getId()+" join: " + gamename);
					
					respuestaP0.put("turn", true);
					respuestaP0.put("opponent", game.getP1Name());
					respuestaP0.put("info", username+" se ha unido a la partida");
					game.getP0Session().sendMessage(new TextMessage(respuestaP0.toString()));
					System.out.println("Message sent to user "+game.getP0Session().getId()+": " + respuestaP0.toString());
					
					respuestaP1.put("turn", false);
					respuestaP1.put("opponent", game.getP0Name());
					respuestaP1.put("gamename", game.getgName());
					respuestaP1.put("info", "Te has unido a: "+ game.getgName());
			}else{
				respuestaP1.put("info", "Error: Ya hay dos jugadores en esa partida.");	
			}
		}else{
			respuestaP1.put("info", "Error: No existe una partida con ese nombre.");
		}

		session.sendMessage(new TextMessage(respuestaP1.toString()));
		System.out.println("Message sent to user "+session.getId()+": " + respuestaP1.toString());
		
	}
	
	private void updateGame(WebSocketSession session, JsonNode node) throws IOException {
		Game game;
		String gamename = node.get("gamename").asText();
		int pos, player;
		int gameState = 3;
		String username, oppname;
		ObjectNode respuesta = mapper.createObjectNode();
		ObjectNode respuestaopp;
		WebSocketSession opponent;
 		
		
		game = games.get(gamename);
			
		if(game.getP1Session()!=null){
			
			if(game.isPlayerTurn(session)){
				pos = Integer.parseInt(node.get("position").asText());
					
				if(game.update(session, pos)){
					respuestaopp = mapper.createObjectNode();
						
					if(session==game.getP0Session()){
						player=0;
						username=game.getP0Name();
						opponent=game.getP1Session();
						oppname=game.getP1Name();
							
					}else{
						player=1;
						username=game.getP1Name();
						opponent=game.getP0Session();
						oppname=game.getP0Name();
					}
						
					respuesta.put("num", player);
					respuesta.put("position", pos);
					respuestaopp.put("num", player);
					respuestaopp.put("position", pos);
					
					gameState=game.getState(player);
						
					if(gameState==player){
						respuesta.put("info", "Has ganado!!");
						respuesta.put("end", "end");
						respuestaopp.put("info", "Ha ganado "+username+"...");	
						respuestaopp.put("end", "end");
						games.remove(gamename);
						System.out.println("Game deleted: "+gamename);
						updateScores(session.getId(),username);
							
					}else if(gameState==2){
						respuesta.put("info", "Tablas.");
						respuesta.put("end", "end");
						respuestaopp.put("info", "Tablas");
						respuestaopp.put("end", "end");
						games.remove(gamename);
						System.out.println("Game deleted: "+gamename);
							
					}else if(gameState!=3){
						respuesta.put("info", "Ha ganado "+oppname+"...");
						respuesta.put("end", "end");
						respuestaopp.put("info", "Has ganado!!");
						respuestaopp.put("end", "end");
						games.remove(gamename);
						System.out.println("Game deleted: "+gamename);
						updateScores(session.getId(),username);
													
					}else{
						respuesta.put("turn", false);
						respuestaopp.put("turn", true);
					}
						
					opponent.sendMessage(new TextMessage(respuestaopp.toString()));
					System.out.println("Message sent to user "+opponent.getId()+": " + respuestaopp.toString());
						
				}else{
					respuesta.put("info", "Error: No puedes poner ahí.");
				}
			}else{
				respuesta.put("info", "Error: No es tu turno.");
			}
		}else{
			respuesta.put("info", "Error: La partida no ha empezado.");
		}
			
		session.sendMessage(new TextMessage(respuesta.toString()));
		System.out.println("Message sent to user "+session.getId()+": " + respuesta.toString());
			
	}
	
	private void updateScores(String id, String name){
		Score sc;
		if(scores.containsKey(id)){
			sc = scores.get(id);
			sc.addVictory();
		}else{
			sc = new Score(name);
			scores.put(id, sc);
		}
		System.out.println("Scores updated: user "+id+" - victories "+scores.get(id).getVictories());
		
	}
	
	static public Collection<Score> getScores(){
		ArrayList<Score> list = new ArrayList<Score>(scores.values());
		
		Collections.sort(list, new Comparator() {
			@Override
			public int compare(Object o1, Object o2) {
				if(o1 instanceof Score){
					Score s1 = (Score)o1;
					Score s2 = (Score)o2;
					return new Integer(s2.getVictories()).compareTo(new Integer(s1.getVictories()));
				}else{
					return 0;
				}

			}
		});
		
		if(list.size()<10){			
			return (Collection<Score>)list.subList(0, list.size());	
		}else{
			return (Collection<Score>)list.subList(0, 10);
		}
	}
		
}

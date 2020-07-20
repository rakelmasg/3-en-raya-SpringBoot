$(document).ready(function() {
	
	var partida="";
	var tableroCopia=[2,2,2,2,2,2,2,2,2];
	var tablero=tableroCopia.slice();
	var username = "";
	
	while(username==""||username==null){
		 username = prompt("Introduce un nombre de usuario:");
	}
	$("#username").html("Usuario: "+username);
	
	var connection = new WebSocket('ws://127.0.0.1:8080/games');
	
	connection.onerror = function(e) {
		console.log("WS error: " + e);
	}
	
	connection.onmessage = function(msg) {
		console.log("WS message: " + msg.data);
		var respuesta = JSON.parse(msg.data);
	
		if(respuesta.gamename!=undefined){
			partida=respuesta.gamename;
			$('#zonaAcceso').css("display","none");
			$('#zonaJuego').css("display","block");
			$('#gamename').html("Partida: "+partida);
		}
		
		if(partida!=""){
			if(respuesta.opponent!=undefined){
				$('#opponent').html("Oponente: "+respuesta.opponent);
			}

			if(respuesta.turn==true){
				$('#turno').html("Tu turno.");	
			}else if(respuesta.turn==false){
				$('#turno').html("Turno del oponente.");		
			}else if(respuesta.turn=="none"){
				$('#turno').html("Esperando a que se una otro jugador...");
			}
			
			if(respuesta.num!=undefined){
				tablero[respuesta.position]=respuesta.num;
			}
			
			pintarTablero(tablero);
		}
		
		if(respuesta.info!=undefined){
			alert(respuesta.info);
		}
		
		if(respuesta.end!=undefined){
			$('#zonaAcceso').css("display","block");
			$('#zonaJuego').css("display","none");
			$('#gamename').html("");
			$('#opponent').html("");
			partida = "";
			tablero=tableroCopia.slice();
		}
		
	}
	
	connection.onclose = function() {
		console.log("Closing socket");
	}

	$('#crear-btn').click(function() {
		if($('#partida').val()==""){
			alert("Error: Introduzca un nombre de partida");
		}else{
			var msg = {
				action : "create",
				username : username,
				gamename : $('#partida').val()
			}
			connection.send(JSON.stringify(msg));
		}
	});
	
	$('#unirse-btn').click(function() {
		if($('#partida').val()==""){
			alert("Error: Introduzca un nombre de partida");
		}else{
			var msg = {
				action : "join",
				username : username,
				gamename : $('#partida').val()
			}
			connection.send(JSON.stringify(msg));
		}
	});	
		
	$('td').click(function() {
		var msg = {
			action : "update",
			gamename : partida,
			position : this.id.charAt(1)
		}
		connection.send(JSON.stringify(msg));
	});
	
	function pintarTablero(tablero){
		 for (var id = 0; id < tablero.length; id++) {
			var selector= "#c"+id;
			var color;
			var ficha="";
			if(tablero[id]==1){
				ficha = "X";
				color = "grey";	
			}else if(tablero[id]==0){
				ficha = "O";
				color = "white";
			}else{
				ficha = "-";
				color = "#ccff99";
			}
			$(selector).html(ficha);
			$(selector).css("color",color);
		}
	}

})
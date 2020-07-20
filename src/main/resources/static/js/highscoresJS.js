//Load items from server
function loadHighscores(callback) {
    $.ajax({
        url: 'http://localhost:8080/highscores'
    }).done(function (highscores) {
        console.log('Highscores loaded: ' + JSON.stringify(highscores));
        callback(highscores);
    })
}


function showHighscore(pos,highscore) {
	$('table').append('<tr><td>'+pos+'.</td><td>' + highscore.name + '</td><td>' 
			+ highscore.victories + '</td></tr>');
}


$(document).ready(function () {
	
	loadHighscores(function (highscores) {
        for (var i = 0; i < highscores.length; i++) {
            showHighscore(i+1,highscores[i]);
        }
    });
         
})

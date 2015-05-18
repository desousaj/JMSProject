<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap.min.css">
		<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap-theme.min.css">
		<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.2/jquery.min.js"></script>
		<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/js/bootstrap.min.js"></script>
		<script src="main.js"></script>
		<style type="text/css">
		    .bs-example{
		    	margin: 20px;
		    }
		</style>
        <title>Accueil </title>
    </head>
    <body onLoad="Chargement();" >         
           <div class="menu">
			    <nav role="navigation" class="navbar navbar-default">
			        <!-- Brand and toggle get grouped for better mobile display -->
			        <div class="navbar-header">
			            <button type="button" data-target="#navbarCollapse" data-toggle="collapse" class="navbar-toggle">
			                <span class="sr-only">Toggle navigation</span>
			                <span class="icon-bar"></span>
			                <span class="icon-bar"></span>
			                <span class="icon-bar"></span>
			            </button>
			            <a href="#" class="navbar-brand">Inscriptions</a>
			        </div>
			        <!-- Collection of nav links and other content for toggling -->
			        <div id="navbarCollapse" class="collapse navbar-collapse">
			            <ul class="nav navbar-nav">
			                <li class="active"><a href="Accueil.jsp">Accueil</a></li>
			                <li><a href="Controleur?action=ajouteInscription">Ajouter</a></li>
			                <li><a href="Controleur?action=afficheInscriptions">Afficher</a></li>
			            </ul>
			        </div>
			    </nav>
			</div>
			
			<h1>Page d'accueil <%= request.getContextPath () %> </h1>
			
    </body>
</html>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
         <style type="text/css">
        label {
          display:block;
           width:150px;
           float:left;
           }
          </style>
          <script src="main.js"></script>
          
    </head>
    
    
    <body onLoad="Chargement();" >   
       <h1>Servlet PostMessage at <%= request.getContextPath () %> </h1>

       <form   action='Controleur?action=envoiInscription'  method='post'>
	         <label for="nom">Nom:</label> <input type='text' name='nom' id="nom">  <br/><br/>
	         <label for="prenom"> Prénom :</label>  <input type='text'name='prenom' id="prenom"> <br/><br/>
	         <label for="datenaissance">Date de naissance : </label> <input type='text' name='datenaissance' id="datenaissance"><br/><br/>
	        <label for="adresse"> Adresse :</label> <input type='text' name='adresse' id="adresse"><br/><br/>
	         <label for="cpostal "> Code Postal :</label> <input type='text' name='cpostal'id ="cpostal"><br/><br/>
	         <label for="ville">  Ville :</label> <input type='text' name='ville'id="ville"><br/><br/>
	           <input type='submit'><br/>
       </form>
    </body>
</html>

<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core_1_1" prefix="c"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
coucou
${nbInscription}
${listeDmdInscription}
<c:forEach  items="${listeDmdInscription}"  var="item" >
							 <tr>
							    <td>${item.nomcandidat}</td>
							    <td>${item.prenomcandidat}</td>
							      <td>
							      <fmt:formatDate type="both" dateStyle="short"
							          timeStyle="short" value="${item.datenaissance}" pattern="dd/MM/yyyy"/>
							      </td>
							       
							      <td>${item.adresse}</td>
								  <td>${item.cpostal}</td>
								   <td>${item.ville}</td>
								  					     
							  </tr>
							 </c:forEach>
</body>
</html>
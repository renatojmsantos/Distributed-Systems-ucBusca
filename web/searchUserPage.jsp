<%@ page import="java.util.ArrayList" %>
<%@ page import="MulticastServer.Webpage" %>
<%@ page import="java.util.Iterator" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <!-- Load icon library -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <meta name = "viewport" content="width=device-width, initial-scale=1">

    <title>ucBusca - Search</title>
    <script src='https://www.google.com/recaptcha/api.js'></script>

    <!-- BOOTSTRAP -->
    <script src="https://code.jquery.com/jquery-3.3.1.slim.min.js" integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo" crossorigin="anonymous"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js" integrity="sha384-UO2eT0CpHqdSJQ6hJty5KVphtPhzWj9WO1clHTMGa3JDZwrnQq4sF86dIHNDz0W1" crossorigin="anonymous"></script>

    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css" integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T" crossorigin="anonymous">
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js" integrity="sha384-JjSmVgyd0p3pXB1rRibZUAYoIIy6OrQ6VrjIEaFf/nJGzIxFDsf4x0xIM+B07jRM" crossorigin="anonymous"></script>

</head>

<body>
<div style="text-align: center;">
    <h1>SEARCH</h1>

    <form action="searchUser" method="post">
        <input type="text" placeholder="pesquisa" name="searchT" required><br>
        <button type="submit"><i class="fa fa-search"></i>Pesquisar!</button>
    </form>


    <jsp:useBean id="res" class="webServer.Action.SearchUserAction">
        <jsp:setProperty name="res" property="*"/>
    </jsp:useBean>


    <p>Search something, ${session.username}. </p>
    <c:choose>
        <c:when test="${session.search == true}">
            Resultado:<br>

            <%
                String aux = String.valueOf(session.getAttribute("username"));
            %>

            <% ArrayList<Webpage> web = res.getSearchUser().searchUser(res.getSearchT(),aux); %>
            <%
                int i=0;
                for (Webpage s : web)
                {
                    out.println("<br>");
                    out.println("<b>"+s.getTitle()+"</b>");
                    out.println("<br>");
                    out.println("<a href="+s.getUrl()+">"+s.getUrl()+"</a>");
                    out.println("<br>");
                    out.println(s.getText());
                    out.println("<br>");
                    i++;
                }
                out.println("<br>");
                out.println("Numero de Resultados: "+i);
            %>
        </c:when>
        <c:otherwise>
            <p></p>
        </c:otherwise>
    </c:choose>

    <form action="backWelcomePage" method="get">
        <button type="submit">Back</button><br>
    </form>
</div>
</body>
</html>


<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1; charset=UTF-8"
         pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <meta name = "viewport" content="width=device-width, initial-scale=1">

    <title>ucBusca</title>
    <script src='https://www.google.com/recaptcha/api.js'></script>

    <!-- BOOTSTRAP -->
    <script src="https://code.jquery.com/jquery-3.3.1.slim.min.js" integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo" crossorigin="anonymous"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js" integrity="sha384-UO2eT0CpHqdSJQ6hJty5KVphtPhzWj9WO1clHTMGa3JDZwrnQq4sF86dIHNDz0W1" crossorigin="anonymous"></script>

    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css" integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T" crossorigin="anonymous">
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js" integrity="sha384-JjSmVgyd0p3pXB1rRibZUAYoIIy6OrQ6VrjIEaFf/nJGzIxFDsf4x0xIM+B07jRM" crossorigin="anonymous"></script>

</head>
<body>
<div style="text-align: center;">
    <br>
    <h1>Welcome</h1>
    <br>
    <c:choose>
        <c:when test="${session.loggedin == true}">
            <p>Welcome, ${session.username}. Do something...</p>
        </c:when>
        <c:otherwise>
            <p>Welcome, anonymous user. Search something...</p>
        </c:otherwise>
    </c:choose>

    <c:choose>
        <c:when test="${session.notificacao == true}">
            <p>Tornou-se administrador!</p>
        </c:when>
        <c:otherwise>
            <p></p>
        </c:otherwise>
    </c:choose>


    <c:choose>
        <c:when test="${session.admin == true}">
            <form action="searchUserPage.jsp">
                <button type="submit">Pesquisar!</button><br>
            </form>

            <form action="giveAdminPage" method="get">
                <button type="submit">Atribuir privilegios</button><br>
            </form>

            <form action="historyPage" method="get">
                <button type="submit">Historico</button><br>
            </form>

            <form action="addLinkPage" method="get">
                <button type="submit">Adicionar link!</button><br>
            </form>

            <form action="linksPage" method="get">
                <button type="submit">Consulta de ligacoes para uma pagina</button><br>
            </form>

            <form action="statsAdminPage.jsp" method="get">
                <button type="submit">Página de administração - Estatísticas</button><br>
            </form>
        </c:when>
        <c:otherwise>
            <form action="searchUserPage.jsp">
                <button type="submit">Pesquisar!</button><br>
            </form>

            <form action="historyPage" method="get">
                <button type="submit">Historico</button><br>
            </form>

            <form action="linksPage" method="get">
                <button type="submit">Consulta de ligacoes para uma pagina</button><br>
            </form>
        </c:otherwise>
    </c:choose>

    <br><br>
    <form action="logout" method="get">
        <button type="submit">Logout</button><br>
    </form>
</div>

</body>
</html>

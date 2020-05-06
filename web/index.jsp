
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
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


  <style>
    .fb{
      background-color: gray;
      color: black;
      text-align: center;
      position: absolute;
      width: 600px;
      height: 300px;
      z-index: 2;
      left: 40px;
      right: 40px;
      top: 300px;
      margin: 0 auto;
      background-color:rgba(255,255,255,.4);/* modern browser */
      -ms-filter:progid:DXImageTransform.Microsoft.gradient(startColorstr=#66FFFFFF,endColorstr=#66FFFFFF);/*IE fix */
      filter:progid:DXImageTransform.Microsoft.gradient(startColorstr=#66FFFFFF,endColorstr=#66FFFFFF);     /* IE Fix */
    }

    /* If the screen size is 600px wide or less, set the font-size of <div> to 30px */
    @media screen and (max-width: 800px) {
      .fb {
        font-size: 15px;
        width: 600px;
        height: 300px;
        z-index: 2;
        left: 20px;
        right: 20px;
        top: 100px;
      }
    }
  </style>

</head>

<body>
<div style="text-align: center;">

  <h1> ucBusca </h1>
  <form action="loginPage" method="get">
    <button type="submit">Login</button><br>
  </form>

  <form action="registerPage" method="get">
    <button type="submit">Registar</button><br>
  </form>

  <form action="searchPage" method="get">
    <button type="submit">Pesquisar</button><br>
  </form>
<br><br>

  <!--
  <form action="loginFacebook" method="get">
    <button type="submit">Login with Facebook</button><br>
  </form>
  -->

  <!--
  <button type="submit" onclick="location.href='https://www.facebook.com/v2.2/dialog/oauth?client_id=852831725181876&redirect_uri=http%3A%2F%2Flocalhost%3A8080%2FucBusca%2FloginFacebook&scope=public_profile'">
    Login with Facebook
  </button>
  -->
  <br>
</div>

</body>
</html>

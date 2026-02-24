<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<t:pageTemplate pageTitle="Products">
    <h1>Products</h1>
    <div class="container text-center">
        <c:forEach var="product" items="${products}">
        <div class="row">
            <div class="col">
                    ${product.name}
            </div>
            <div class="col">
                    ${product.all_time_low}
            </div>
            <div class="col">
                    ${product.image_url}
            </div>
        </div>
        </c:forEach>
    </div>

</t:pageTemplate>
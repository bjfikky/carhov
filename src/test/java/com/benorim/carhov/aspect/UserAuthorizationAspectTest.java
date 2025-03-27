package com.benorim.carhov.aspect;

import com.benorim.carhov.security.services.UserDetailsImpl;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserAuthorizationAspectTest {

    private UserAuthorizationAspect aspect;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private UserDetailsImpl userDetails;

    @Mock
    private ProceedingJoinPoint joinPoint;

    @Mock
    private MethodSignature methodSignature;

    @BeforeEach
    void setUp() {
        aspect = new UserAuthorizationAspect();
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void checkUserOwnership_WhenUserOwnsResource_ShouldProceed() throws Throwable {
        // Arrange
        Long userId = 1L;
        when(securityContext.getAuthentication()).thenReturn(mock(org.springframework.security.core.Authentication.class));
        when(securityContext.getAuthentication().getPrincipal()).thenReturn(userDetails);
        when(userDetails.getId()).thenReturn(userId);
        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(methodSignature.getMethod()).thenReturn(getClass().getMethod("testMethod", Long.class));
        when(joinPoint.getArgs()).thenReturn(new Object[]{userId});

        // Act
        Object result = aspect.checkUserOwnership(joinPoint);

        // Assert
        assertNull(result);
        verify(securityContext, atLeastOnce()).getAuthentication();
        verify(userDetails).getId();
        verify(joinPoint).proceed();
    }

    @Test
    void checkUserOwnership_WhenUserDoesNotOwnResource_ShouldReturnForbidden() throws Throwable {
        // Arrange
        Long userId = 1L;
        Long differentUserId = 2L;
        when(securityContext.getAuthentication()).thenReturn(mock(Authentication.class));
        when(securityContext.getAuthentication().getPrincipal()).thenReturn(userDetails);
        when(userDetails.getId()).thenReturn(differentUserId);
        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(methodSignature.getMethod()).thenReturn(getClass().getMethod("testMethod", Long.class));
        when(joinPoint.getArgs()).thenReturn(new Object[]{userId});

        // Act
        Object result = aspect.checkUserOwnership(joinPoint);

        // Assert
        assertNotNull(result);
        assertInstanceOf(ResponseEntity.class, result);
        ResponseEntity<?> response = (ResponseEntity<?>) result;
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(securityContext, atLeastOnce()).getAuthentication();
        verify(userDetails, atLeastOnce()).getId();
        verify(joinPoint, never()).proceed();
    }

    @Test
    void checkUserOwnership_WhenNoAuthentication_ShouldReturnForbidden() throws Throwable {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(null);
        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(methodSignature.getMethod()).thenReturn(getClass().getMethod("testMethod", Long.class));
        when(joinPoint.getArgs()).thenReturn(new Object[]{1L});

        // Act
        Object result = aspect.checkUserOwnership(joinPoint);

        // Assert
        assertNotNull(result);
        assertTrue(result instanceof ResponseEntity);
        ResponseEntity<?> response = (ResponseEntity<?>) result;
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(securityContext).getAuthentication();
        verify(joinPoint, never()).proceed();
    }

    @Test
    void checkUserOwnership_WhenPrincipalIsNotUserDetails_ShouldReturnForbidden() throws Throwable {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(mock(org.springframework.security.core.Authentication.class));
        when(securityContext.getAuthentication().getPrincipal()).thenReturn("not a UserDetails object");
        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(methodSignature.getMethod()).thenReturn(getClass().getMethod("testMethod", Long.class));
        when(joinPoint.getArgs()).thenReturn(new Object[]{1L});

        // Act
        Object result = aspect.checkUserOwnership(joinPoint);

        // Assert
        assertNotNull(result);
        assertTrue(result instanceof ResponseEntity);
        ResponseEntity<?> response = (ResponseEntity<?>) result;
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(securityContext, atLeastOnce()).getAuthentication();
        verify(joinPoint, never()).proceed();
    }

    @Test
    void checkUserOwnership_WhenNoPathVariable_ShouldReturnInternalServerError() throws Throwable {
        // Arrange
        Long userId = 1L;
        when(securityContext.getAuthentication()).thenReturn(mock(org.springframework.security.core.Authentication.class));
        when(securityContext.getAuthentication().getPrincipal()).thenReturn(userDetails);
        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(methodSignature.getMethod()).thenReturn(getClass().getMethod("testMethodWithoutPathVariable", String.class));
        when(joinPoint.getArgs()).thenReturn(new Object[]{"test"});

        // Act
        Object result = aspect.checkUserOwnership(joinPoint);

        // Assert
        assertNotNull(result);
        assertTrue(result instanceof ResponseEntity);
        ResponseEntity<?> response = (ResponseEntity<?>) result;
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(securityContext, atLeastOnce()).getAuthentication();
        verify(joinPoint, never()).proceed();
    }

    // Test methods for reflection
    @RequireUserOwnership
    public void testMethod(@PathVariable("userId") Long userId) {
        // Test method
    }

    @RequireUserOwnership
    public void testMethodWithoutPathVariable(String param) {
        // Test method
    }
} 
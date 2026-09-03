package com.niluverse.uninex.auth;

/**
 * Application-level roles. Every user starts as STUDENT on first Google
 * login; STAFF/ADMIN must be granted manually (in the database) by an
 * existing admin -- there is no self-service way to promote yourself.
 */
public enum Role {
    STUDENT,
    STAFF,
    ADMIN
}

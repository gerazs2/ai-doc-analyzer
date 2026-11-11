package com.jgmagalop.aidocanalyzer.security;

import com.jgmagalop.aidocanalyzer.security.user.Department;
import com.jgmagalop.aidocanalyzer.security.user.Role;

public record AuthenticatedUser(Long id, String email, Role role, Department department) {
}
package org.funding.project.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final org.funding.project.dao.ProjectDAO projectDAO;

}
package com.example.rosaceae.repository;
import com.example.rosaceae.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ReportRepo extends JpaRepository<Report, Integer> {
}

package com.freelancego.service.FreelancerService;

import com.freelancego.dto.client.JobDto;
import com.freelancego.dto.freelancer.BidDto;
import com.freelancego.dto.freelancer.BrowseJobDto;
import com.freelancego.dto.freelancer.FreelancerDto;
import com.freelancego.dto.user.ContractDto;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface FreelancerService {

    public Map<String, Object> createFreelancer(FreelancerDto freelancerDto, String username);

    List<BrowseJobDto> getBrowseJobs(int page, int size, String name);

    List<JobDto> getPostByStatus(int page, int size, String name);

    List<ContractDto> getPostByPhase(String name);

    Map<String, Object> getBidHistory(int page, int size,String name);

    Map<String, Object> getEarningsDashboard(int page, int size, String email);

    Map<String,Object> getAnalytics(String name);

}

/*
public Map<String, Object> freelancerReviewAnalytics(List<Review> reviews) {
    int totalReviews = reviews == null ? 0 : reviews.size();

    // 1. Average rating
    double avgRating = totalReviews == 0 ? 0.0 :
            reviews.stream()
                   .mapToInt(Review::getRating)
                   .average()
                   .orElse(0.0);

    // 2. Ratings breakdown (count per rating 1-5)
    Map<Integer, Long> ratingBreakdown = reviews == null ? Map.of() :
            reviews.stream()
                   .collect(Collectors.groupingBy(
                       Review::getRating,
                       Collectors.counting()
                   ));

    // 3. Percentage of 5-star reviews
    double fiveStarPercent = totalReviews == 0 ? 0.0 :
            ((double) ratingBreakdown.getOrDefault(5, 0L) * 100) / totalReviews;

    // 4. Latest review comments (last 3 reviews)
    List<String> latestComments = reviews == null ? List.of() :
            reviews.stream()
                   .sorted(Comparator.comparing(r -> r.getContract().getCreatedAt(), Comparator.reverseOrder()))
                   .limit(3)
                   .map(Review::getComment)
                   .collect(Collectors.toList());

    Map<String, Object> response = new LinkedHashMap<>();
    response.put("totalReviews", totalReviews);
    response.put("avgRating", avgRating);
    response.put("ratingBreakdown", ratingBreakdown);
    response.put("fiveStarPercent", fiveStarPercent);
    response.put("latestComments", latestComments);

    return response;
}

 */
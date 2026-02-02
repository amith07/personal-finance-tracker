package com.ey.pft.category;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CategorySeeder {

	@Bean
	public ApplicationRunner seedDefaultCategories(CategoryRepository categoryRepository) {
		return args -> {
			// EXPENSE defaults
			seedIfMissing(categoryRepository, CategoryType.EXPENSE, "Food");
			seedIfMissing(categoryRepository, CategoryType.EXPENSE, "Rent");
			seedIfMissing(categoryRepository, CategoryType.EXPENSE, "Travel");
			seedIfMissing(categoryRepository, CategoryType.EXPENSE, "Utilities");

			// INCOME defaults
			seedIfMissing(categoryRepository, CategoryType.INCOME, "Salary");

			// TRANSFER defaults
			seedIfMissing(categoryRepository, CategoryType.TRANSFER, "Transfer");
		};
	}

	private void seedIfMissing(CategoryRepository repo, CategoryType type, String name) {
		boolean exists = repo.findSystemByTypeAndName(type, name).isPresent();
		if (!exists) {
			Category c = new Category();
			c.setUser(null); // system category
			c.setName(name);
			c.setType(type);
			c.setParent(null);
			repo.save(c);
		}
	}
}
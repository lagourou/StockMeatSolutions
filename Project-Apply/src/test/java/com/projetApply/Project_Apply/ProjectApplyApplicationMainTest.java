package com.projetApply.Project_Apply;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;

class ProjectApplyApplicationMainTest {

	@Test
	void main_shouldInvokeSpringApplicationRun() {
		// On mock SpringApplication.run pour éviter de démarrer le contexte complet
		try (var mocked = mockStatic(SpringApplication.class)) {
			mocked.when(() -> SpringApplication.run(any(Class.class), any(String[].class)))
					.thenReturn(null);

			// On vérifie que main() ne lance pas d'exception
			assertDoesNotThrow(() -> ProjectApplyApplication.main(new String[] { "arg1", "arg2" }));

			// On vérifie que SpringApplication.run a bien été appelé avec la bonne classe
			// et les bons arguments
			mocked.verify(() -> SpringApplication.run(ProjectApplyApplication.class, new String[] { "arg1", "arg2" }));
		}
	}
}

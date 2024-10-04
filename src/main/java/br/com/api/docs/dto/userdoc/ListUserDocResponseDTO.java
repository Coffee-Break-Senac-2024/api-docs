package br.com.api.docs.dto.userdoc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListUserDocResponseDTO {
    private List<UserDocResponseDTO> documents;
}

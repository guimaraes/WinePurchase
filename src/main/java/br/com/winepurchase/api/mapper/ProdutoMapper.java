package br.com.winepurchase.api.mapper;

import br.com.winepurchase.api.model.domain.ProdutoDomain;
import br.com.winepurchase.api.model.dto.ProdutoDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ProdutoMapper {
    ProdutoMapper INSTANCE = Mappers.getMapper(ProdutoMapper.class);

    ProdutoDTO toDto(ProdutoDomain produto);
    ProdutoDomain toDomain(ProdutoDTO produtoDto);
}

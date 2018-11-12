package com.aotain.zongfen.model.sankey;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SanKey {

    private List<Nodes> nodes;
    private List<Links> links;
}

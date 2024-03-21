package com.example.financeback.classes

class NotaFiscal (usuarioID: Int? = null){
    private var usuarioID: Int? = if (usuarioID == null) usuarioID else null
    private var notaID: Int? = null
    var valor: Double? = null
    var nome: String? = null
    var data: String? = null
    var lucro: Boolean = false

    fun salvarNota(valor: Double, nome: String, data: String, lucro: Boolean) {
//        Salvar nota com valor X, nome X, data x e se for ou nao lucro
    }

    fun excluirNota(usuarioID: Int? = this.usuarioID, notaID: Int? = this.notaID){
//        Excluir nota x do usuario x
    }

    fun atualizarValorTotal(usuarioID: Int? = this.usuarioID){
//        Atualizar o valor total do usuario X pegando do banco todas as notas fiscais atribuida a ele dentro de um mes
    }

    fun gerarRelatorio(usuarioID: Int? = this.usuarioID){
//        Listar e calcular o total de ganho, perda e valor total do usuario X dentro de um mes
    }

    fun exportarRelatorio(usuarioID: Int? = this.usuarioID){
//        Exportar em o relatorio do usuario X
    }

}
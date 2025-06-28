import {Component} from '@angular/core';
import {Filtro} from "./filtro.model";
import {DocumentoService} from "./documento.service";
import {Documento} from "./documento.model";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'frontend';


  filtro: Filtro = {
    texto: '',
  }

  documentos: Documento[] = [
    {
      id: "1",
      content: "Conteudo",
      fileName: "pdf"
    }
  ];


  constructor(private documentoService: DocumentoService) {
  }


  pesquisar() {

    console.log('Pesquisar por: ', this.filtro);

    this.documentoService.buscarPorConteudo(this.filtro).subscribe(response => {
      this.documentos = response;
      console.log('Response: ', this.documentos);
    });

  }
}

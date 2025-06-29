import {Component} from '@angular/core';
import {Filtro} from "./filtro.model";
import {DocumentoService} from "./documento.service";
import {Documento} from "./documento.model";
import {NgxExtendedPdfViewerService} from "ngx-extended-pdf-viewer";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
  providers: [NgxExtendedPdfViewerService],
})
export class AppComponent {
  title = 'frontend';


  filtro: Filtro = {
    texto: '',
  }


  documentos: Documento[] = [];
  showDialog: boolean = true;


  constructor(private documentoService: DocumentoService,
              private pdfService: NgxExtendedPdfViewerService) {
  }


  pesquisar() {

    console.log('Pesquisar por: ', this.filtro);

    this.documentoService.buscarPorConteudo(this.filtro).subscribe(response => {
      this.documentos = response;
      console.log('Response: ', this.documentos);
    });

  }

  visualizar(id: string) {
    console.log('Visualziar por: ', id);
  }


}

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

  pdfbase64: string = "";

  filtro: Filtro = {
    texto: '',
  }


  documentos: Documento[] = [];
  showDialog: boolean = false;
  visible: boolean = true;


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


  visualizar(uuid: string) {
    console.log('Visualziar por: ', uuid);

    this.documentoService.visualizarFromUUID(uuid).subscribe(response => {
      this.pdfbase64 = response;
      this.showDialog = true;
    })
  }

  onFileSelected(event: any) {
    const file: File = event.target.files[0];

    this.documentoService.uploadPdf(file).subscribe({
      next: (res) => {
        console.log('Arquivo salvo com UUID:', res.uuid);
        // this.uuidDoArquivo = res.uuid;
      },
      error: (err) => {
        console.error('Erro ao enviar arquivo', err);
      }
    });
  }


}

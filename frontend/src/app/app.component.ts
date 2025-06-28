import {Component} from '@angular/core';
import {Filtro} from "./filtro.model";
import {DocumentoService} from "./documento.service";

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


  constructor(private documentoService: DocumentoService) {
  }


  pesquisar() {

    console.log('Pesquisar por: ', this.filtro);

    this.documentoService.buscarPorConteudo(this.filtro).subscribe(response => {
      console.log('Response: ', response);
    });

  }
}

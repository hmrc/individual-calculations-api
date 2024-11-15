package v7.common.model.domain

sealed trait TysDownstream

case object Pre24Downstream extends TysDownstream
case object Either24or25Downstream extends TysDownstream
case object Post26Downstream extends TysDownstream

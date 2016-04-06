<?php

include_once 'common.php';
include_once 'entities/Type.php';
include_once 'entities/Chronological.php';
include_once 'entities/Purchase.php';
include_once 'entities/Detail.php';

class AHandlerSendEntity
  extends ABaseHandler
  implements I_Transaction
{
  const INSERTED     = 0;
  const UPDATED      = 1;
  const NOT_LAST_REV = 2;

  protected $link;
  private $enc = "";
  public function __construct()
  {
    global $link;
    $DB = new MySQLOpenHelper();
    $link = $DB->getLink();
  }

  protected function AHandling($JOBJ)
  {
    global $link; //Такое извращение нужно что бы видеть глобальные переменные
    $transaction = new SQLTransaction($link, $this);
    $params = array("JOBJ"=>$JOBJ);
    $transaction->runTransaction($params);
    return $this->enc;
  }
  public function trnFunc($params)
  {
    global $link;
    $user_id = $_SESSION[GLOBALS_VAR\ID];
    $user_account = UserAccount::getUserAccountFromID($link, $user_id);
    $JOBJ = $params["JOBJ"];
    $timestamp_cl = $JOBJ->{'TIMESTAMP'};
    $revision     = $JOBJ->{'REVISION'};
    $table        = $JOBJ->{'TABLE'};
    $entity       = $JOBJ->{'ENTITY'};

    $result = true;
    if($revision < Chronological::getLastRevision($user_account->_id_group, $link))
    {
      $arr = array('MESSAGE' => E_MESSAGEID\SEND_ENTITY, 'RESULT' => AHandlerSendEntity::NOT_LAST_REV);
      $this->enc = json_encode($arr);
      $result = false;
    }
    if($result)
    {
      switch ($table)
      {
        case E_TABLE\TYPE:
          $_id = $entity->{'id_server'};
          if(!isset($_id)) //Если клиенту не известен id_server
          {
            $type = new Type(Type::$ENUM_CONSTRUCT_JObj, $user_account->_id_group, $entity);
            $type->_id = 1 + Type::getLast_id($user_account->_id_group, $link);
            if (($result = $result && $type->insert($link))) //Вставили запись, вернем id на сервере
            {
              //Записываем в хронологию об изменениях
              $chronological = new Chronological(Chronological::$ENUM_CONSTRUCT_FIELD, $user_account->_id_group,
                0, $table, $type->_id, $JOBJ->{'TIMESTAMP'});
              $result = $result && $chronological->insert($link);
              //Ответ о том что вставили запись
              if($result && $chronological->revision == ($revision + 1))
              {
                $arr = array('MESSAGE' => E_MESSAGEID\SEND_ENTITY, 'RESULT' => AHandlerSendEntity::INSERTED,
                  '_id_server' => $type->_id, 'REVISION' => $chronological->revision);
                $this->enc = json_encode($arr);
              }
              else
              {
                $arr = array('MESSAGE' => E_MESSAGEID\SEND_ENTITY, 'RESULT' => AHandlerSendEntity::NOT_LAST_REV);
                $this->enc = json_encode($arr);
                $result = false;
              }
            }
            else
            {
              $type = Type::getTypeFromName($user_account->_id_group, $entity->{'name'}, $link);
              if($type != null)
              {
                $chronological = Chronological::getLastRevision($user_account->_id_group, $link);
                $arr = array('MESSAGE' => E_MESSAGEID\SEND_ENTITY, 'RESULT' => AHandlerSendEntity::INSERTED,
                  '_id_server' => $type->_id, 'REVISION' => $chronological->revision);
                $this->enc = json_encode($arr);
              }
              else
              {
                $arr = array('MESSAGE' => E_MESSAGEID\SEND_ENTITY, 'RESULT' => AHandlerSendEntity::NOT_LAST_REV);
                $this->enc = json_encode($arr);
                $result = false;
              }
            }
          }
          else
          {
            $type = Type::getTypeFrom_id($user_account->_id_group, $_id, $link);
            $new_type = $type->copy();
            $new_type->is_delete = $entity->{'is_delete'};
            $new_type->id_unit = $entity->{'id_unit'};
            $new_type->name = $entity->{'name'};
            if(($result = $result && $type->update($link, $new_type)))
            {
              //Записываем в хронологию об изменениях
              $chronological = new Chronological(Chronological::$ENUM_CONSTRUCT_FIELD, $user_account->_id_group,
                0, $table, $type->_id, $JOBJ->{'TIMESTAMP'});
              $result = $result && $chronological->insert($link);
              //Ответ о том что обновили запись
              if($result && $chronological->revision == ($revision + 1))
              {
                $arr = array('MESSAGE' => E_MESSAGEID\SEND_ENTITY, 'RESULT' => AHandlerSendEntity::UPDATED,
                  '_id_server' => $type->_id, 'REVISION' => $chronological->revision);
                $this->enc = json_encode($arr);
              }
              else
              {
                $arr = array('MESSAGE' => E_MESSAGEID\SEND_ENTITY, 'RESULT' => AHandlerSendEntity::NOT_LAST_REV);
                $this->enc = json_encode($arr);
                $result = false;
              }
            }
          }
          break;
        case E_TABLE\DETAIL:
          $_id = $entity->{'id_server'};
          if(!isset($_id)) //Если клиенту не известен id_server
          {
            $detail = new Detail(Detail::$ENUM_CONSTRUCT_JObj, $user_account->_id_group, $entity);
            $detail->_id = 1 + Detail::getLast_id($user_account->_id_group, $link);
            if (($result = $result && $detail->insert($link))) //Вставили запись, вернем id на сервере
            {
              //Записываем в хронологию об изменениях
              $chronological = new Chronological(Chronological::$ENUM_CONSTRUCT_FIELD, $user_account->_id_group,
                0, $table, $detail->_id, $JOBJ->{'TIMESTAMP'});
              $result = $result && $chronological->insert($link);
              //Ответ о том что вставили запись
              if($result && $chronological->revision == ($revision + 1))
              {
                $arr = array('MESSAGE' => E_MESSAGEID\SEND_ENTITY, 'RESULT' => AHandlerSendEntity::INSERTED,
                  '_id_server' => $detail->_id, 'REVISION' => $chronological->revision);
                $this->enc = json_encode($arr);
              }
              else
              {
                $arr = array('MESSAGE' => E_MESSAGEID\SEND_ENTITY, 'RESULT' => AHandlerSendEntity::NOT_LAST_REV);
                $this->enc = json_encode($arr);
                $result = false;
              }
            }
            else
            {
              $detail = Detail::getDetailFromPurchaseAndType($user_account->_id_group, $entity->{'_id_purchase'},
                $entity->{'_id_type'}, $link);
              if($detail != null)
              {
                $chronological = Chronological::getLastRevision($user_account->_id_group, $link);
                $arr = array('MESSAGE' => E_MESSAGEID\SEND_ENTITY, 'RESULT' => AHandlerSendEntity::INSERTED,
                  '_id_server' => $detail->_id, 'REVISION' => $chronological->revision);
                $this->enc = json_encode($arr);
              }
              else
              {
                $arr = array('MESSAGE' => E_MESSAGEID\SEND_ENTITY, 'RESULT' => AHandlerSendEntity::NOT_LAST_REV);
                $this->enc = json_encode($arr);
                $result = false;
              }
            }
          }
          else
          {
            $detail = Detail::getDetailFrom_id($user_account->_id_group, $_id, $link);
            $new_rec = $detail->copy();
            $new_rec->_id_purchase    = $entity->{'_id_purchase'};
            $new_rec->_id_type        = $entity->{'_id_type'};
            $new_rec->price           = $entity->{'price'};
            $new_rec->for_amount_unit = $entity->{'for_amount_unit'};
            $new_rec->for_id_unit     = $entity->{'for_id_unit'};
            $new_rec->amount          = $entity->{'amount'};
            $new_rec->id_unit         = $entity->{'id_unit'};
            $new_rec->cost            = $entity->{'cost'};
            $new_rec->is_delete       = $entity->{'is_delete'};
            if(($result = $result && $detail->update($link, $new_rec)))
            {
              //Записываем в хронологию об изменениях
              $chronological = new Chronological(Chronological::$ENUM_CONSTRUCT_FIELD, $user_account->_id_group,
                0, $table, $detail->_id, $JOBJ->{'TIMESTAMP'});
              $result = $result && $chronological->insert($link);
              //Ответ о том что обновили запись
              if($result && $chronological->revision == ($revision + 1))
              {
                $arr = array('MESSAGE' => E_MESSAGEID\SEND_ENTITY, 'RESULT' => AHandlerSendEntity::UPDATED,
                  '_id_server' => $detail->_id, 'REVISION' => $chronological->revision);
                $this->enc = json_encode($arr);
              }
              else
              {
                $arr = array('MESSAGE' => E_MESSAGEID\SEND_ENTITY, 'RESULT' => AHandlerSendEntity::NOT_LAST_REV);
                $this->enc = json_encode($arr);
                $result = false;
              }
            }
          }
          break;
        case E_TABLE\PURCHASE:
          $_id = $entity->{'id_server'};
          if(!isset($_id)) //Если клиенту не известен id_server
          {
            $purchase = new Purchase(Purchase::$ENUM_CONSTRUCT_JObj, $user_account->_id_group, $entity);
            $purchase->_id = 1 + Purchase::getLast_id($user_account->_id_group, $link);
            if (($result = $result && $purchase->insert($link))) //Вставили запись, вернем id на сервере
            {
              //Записываем в хронологию об изменениях
              $chronological = new Chronological(Chronological::$ENUM_CONSTRUCT_FIELD, $user_account->_id_group,
                0, $table, $purchase->_id, $JOBJ->{'TIMESTAMP'});
              $result = $result && $chronological->insert($link);
              //Ответ о том что вставили запись
              if($result && $chronological->revision == ($revision + 1))
              {
                $arr = array('MESSAGE' => E_MESSAGEID\SEND_ENTITY, 'RESULT' => AHandlerSendEntity::INSERTED,
                  '_id_server' => $purchase->_id, 'REVISION' => $chronological->revision);
                $this->enc = json_encode($arr);
              }
              else
              {
                $arr = array('MESSAGE' => E_MESSAGEID\SEND_ENTITY, 'RESULT' => AHandlerSendEntity::NOT_LAST_REV);
                $this->enc = json_encode($arr);
                $result = false;
              }
            }
          }
          else
          {
            $purchase = Purchase::getPurchaseFrom_id($user_account->_id_group, $_id, $link);
            $new_rec = $purchase->copy();
            $new_rec->date_time = $entity->{'date_time'};
            $new_rec->state = $entity->{'state'};
            $new_rec->is_delete = $entity->{'is_delete'} ? 1 : 0;
            if(($result = $result && $purchase->update($link, $new_rec)))
            {
              //Записываем в хронологию об изменениях
              $chronological = new Chronological(Chronological::$ENUM_CONSTRUCT_FIELD, $user_account->_id_group,
                0, $table, $purchase->_id, $JOBJ->{'TIMESTAMP'});
              $result = $result && $chronological->insert($link);
              //Ответ о том что обновили запись
              if($result && $chronological->revision == ($revision + 1))
              {
                $arr = array('MESSAGE' => E_MESSAGEID\SEND_ENTITY, 'RESULT' => AHandlerSendEntity::UPDATED,
                  '_id_server' => $purchase->_id, 'REVISION' => $chronological->revision);
                $this->enc = json_encode($arr);
              }
              else
              {
                $arr = array('MESSAGE' => E_MESSAGEID\SEND_ENTITY, 'RESULT' => AHandlerSendEntity::NOT_LAST_REV);
                $this->enc = json_encode($arr);
                $result = false;
              }
            }
          }
          break;
      }
    }
    return $result;
  }
}
?>